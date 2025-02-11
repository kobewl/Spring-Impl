package com.spring;

/**
 * Bean 后置处理器接口
 * 用于在 Bean 实例化前后进行自定义处理
 */
public interface BeanPostProcessor {

    /**
     * 在 Bean 初始化之前执行
     * 
     * @param bean     Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的 Bean
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * 在 Bean 初始化之后执行
     * 
     * @param bean     Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的 Bean
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}