package com.spring;

/**
 * Bean 初始化接口
 * 实现此接口的 Bean 在初始化时会调用 afterPropertiesSet 方法
 */
public interface InitializingBean {

    /**
     * Bean 属性设置完成后调用
     */
    void afterPropertiesSet() throws Exception;
}