package com.spring;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;

/**
 * Spring IoC 容器实现
 */
public class ImplApplicationContext {

    private Class configClass;

    // 一级缓存：完整的单例 Bean
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 二级缓存：提前暴露的单例对象（未完全初始化）
    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 三级缓存：单例工厂对象
    private Map<String, ObjectFactory> singletonFactories = new ConcurrentHashMap<>();

    // 正在创建中的 Bean 名称集合
    private Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    // BeanPostProcessor 列表
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    // Bean 定义信息的缓存
    private Map<String, Class<?>> beanDefinitionMap = new ConcurrentHashMap<>();

    public ImplApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 扫描组件
        scanComponents();

        // 创建单例 Bean
        createSingletonBeans();
    }

    private void scanComponents() {
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value().replace(".", "/");

        // 使用 Reflections 扫描指定包下的所有类
        Reflections reflections = new Reflections(path);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);

        // 注册 Bean 定义
        for (Class<?> cls : components) {
            Component component = cls.getAnnotation(Component.class);
            String beanName = component.value();
            if (beanName.isEmpty()) {
                // 如果没有指定 bean 名称，使用类名首字母小写作为 bean 名称
                beanName = Character.toLowerCase(cls.getSimpleName().charAt(0)) +
                        cls.getSimpleName().substring(1);
            }
            beanDefinitionMap.put(beanName, cls);

            // 注册 BeanPostProcessor
            if (BeanPostProcessor.class.isAssignableFrom(cls)) {
                try {
                    BeanPostProcessor processor = (BeanPostProcessor) cls.getDeclaredConstructor().newInstance();
                    beanPostProcessors.add(processor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建所有单例 Bean
     */
    private void createSingletonBeans() {
        for (String beanName : beanDefinitionMap.keySet()) {
            getBean(beanName);
        }
    }

    public Object getBean(String beanName) {
        try {
            // 先尝试从一级缓存获取完整的 Bean
            Object singleton = singletonObjects.get(beanName);
            if (singleton != null) {
                return singleton;
            }

            // 如果 Bean 正在创建中，说明发生了循环依赖
            if (singletonsCurrentlyInCreation.contains(beanName)) {
                // 尝试从二级缓存获取早期对象
                singleton = earlySingletonObjects.get(beanName);
                if (singleton != null) {
                    return singleton;
                }
                // 尝试从三级缓存获取工厂对象并创建早期对象
                ObjectFactory factory = singletonFactories.get(beanName);
                if (factory != null) {
                    try {
                        singleton = factory.getObject();
                        // 放入二级缓存
                        earlySingletonObjects.put(beanName, singleton);
                        // 从三级缓存移除
                        singletonFactories.remove(beanName);
                        return singleton;
                    } catch (Exception e) {
                        throw new RuntimeException("Error creating bean from factory: " + beanName, e);
                    }
                }
            }

            // 开始创建 Bean
            Class<?> beanClass = beanDefinitionMap.get(beanName);
            if (beanClass == null) {
                throw new RuntimeException("Bean not found: " + beanName);
            }

            // 标记 Bean 正在创建中
            singletonsCurrentlyInCreation.add(beanName);

            try {
                // 实例化 Bean
                Object bean = doCreateBean(beanClass);

                // 添加到三级缓存
                final Object finalBean = bean;
                singletonFactories.put(beanName, new ObjectFactory() {
                    @Override
                    public Object getObject() {
                        // 这里可以进行 AOP 代理
                        return finalBean;
                    }
                });

                // 属性注入（可能触发循环依赖）
                try {
                    populateBean(bean);
                } catch (Exception e) {
                    throw new RuntimeException("Error populating bean: " + beanName, e);
                }

                // 初始化
                try {
                    bean = initializeBean(bean, beanName);
                } catch (Exception e) {
                    throw new RuntimeException("Error initializing bean: " + beanName, e);
                }

                // 将完整的 Bean 放入一级缓存
                singletonObjects.put(beanName, bean);
                // 从二级和三级缓存中移除
                earlySingletonObjects.remove(beanName);
                singletonFactories.remove(beanName);

                return bean;
            } catch (Exception e) {
                throw new RuntimeException("Error creating bean: " + beanName, e);
            } finally {
                // 移除创建中标记
                singletonsCurrentlyInCreation.remove(beanName);
            }
        } catch (Exception e) {
            throw new BeanCreationException("Error getting bean: " + beanName, e);
        }
    }

    private Object doCreateBean(Class<?> beanClass) throws Exception {
        return beanClass.getDeclaredConstructor().newInstance();
    }

    /**
     * 属性注入
     * 
     * @param bean
     * @throws Exception
     */
    private void populateBean(Object bean) throws Exception {
        // 使用反射获取字段
        Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历字段，查找是否有 @Autowired 注解
        for (Field field : fields) {
            // 检查字段是否被 @Autowired 注解
            if (field.isAnnotationPresent(Autowired.class)) {
                // 设置字段可访问
                field.setAccessible(true);

                // 获取 @Autowired 注解
                Autowired autowired = field.getAnnotation(Autowired.class);
                // 如果 required 为 true，则获取对应的 Bean 并注入字段
                if (autowired.required()) {
                    if (field.getType().isInterface()) {
                        // 如果字段是接口，则使用 JDK 动态代理创建实现类
                        // TODO: 实现 JDK 动态代理逻辑
                    } else {
                        // 如果字段是具体类，则使用 CGLIB 动态代理创建子类
                        // TODO: 实现 CGLIB 动态代理逻辑
                    }
                    // 从一级缓存中获取对应的 Bean
                    Object value = getBean(field.getName());
                    if (value == null) {
                        // 如果对应的 Bean 不存在，则抛出异常
                        throw new RuntimeException("No bean found with name '" + field.getName() + "'");
                    }
                    // 将字段注入到 bean 中
                    field.set(bean, value);
                }
            }
        }
    }

    /**
     * 初始化 Bean
     * 
     * @param bean     实例化的 Bean
     * @param beanName Bean 的名称
     * @return Object
     * @throws Exception 抛出异常
     */
    private Object initializeBean(Object bean, String beanName) throws Exception {
        // 执行 BeanPostProcessor 前置处理
        Object current = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            current = processor.postProcessBeforeInitialization(current, beanName);
        }

        // 调用初始化方法
        // instanceof 是 Java 关键字，用于判断对象是否是某个类或接口的实例。
        if (bean instanceof InitializingBean) {
            // 如果 bean 是 InitializingBean 类型的实例，则执行 afterPropertiesSet() 方法。
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 执行 BeanPostProcessor 后置处理
        for (BeanPostProcessor processor : beanPostProcessors) {
            current = processor.postProcessAfterInitialization(current, beanName);
        }

        return current;
    }

    // 用于解决循环依赖的工厂接口
    interface ObjectFactory {
        Object getObject() throws Exception;
    }

    /**
     * Bean 创建异常
     */
    public static class BeanCreationException extends RuntimeException {
        public BeanCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
