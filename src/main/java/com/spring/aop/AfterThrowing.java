package com.spring.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异常通知注解
 * 在目标方法抛出异常时执行
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterThrowing {
    /**
     * 切入点表达式
     */
    String value();

    /**
     * 异常类型
     */
    Class<? extends Throwable> throwing() default Throwable.class;
}