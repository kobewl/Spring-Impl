package com.spring;


import java.io.File;
import java.net.URL;

import org.reflections.Reflections;

public class ImplApplicationContext {

    private Class configClass;

   public ImplApplicationContext(Class configClass){
       this.configClass = configClass;

       // 解析配置类
       // ComponentScan注解-->扫描路径-->扫描
       ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
       // 获得属性
       String path = componentScanAnnotation.value();
       path = path.replace(".", "/");
       System.out.println(path);

       // 扫描包
       // 类加载的三种方式：
       // Bootstrap     jre/lib
       // Ext           jre/ext/lib
       // App           classpath
       ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
       // 取得绝对路径: /Users/mafei007/AppProjects/IdeaProjects/spring_study/out/production/simple_impl/com/mafei/test
       URL resource = classLoader.getResource(path);
       File file = new File(resource.getFile());
       // 使用Reflections扫描指定包下的所有类
       Reflections reflections = new Reflections(path);
       for (Class<?> scannedClass : reflections.getTypesAnnotatedWith(Component.class)) {
           String className = scannedClass.getName();
           System.out.println(className);
       }
       // 遍历目录下的所有文件，都是 componentScan 需要扫描的，这里只遍历了一层目录
       //if (file.isDirectory()) {
       //    for (File f : file.listFiles()) {
       //        String fileName = f.getAbsolutePath();
       //        System.out.println(fileName);
       //    }
       //}
   }


   public Object getBean(String beanName){
       return null;
   }

}
