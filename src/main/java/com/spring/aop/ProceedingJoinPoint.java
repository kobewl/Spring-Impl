package com.spring.aop;

import java.lang.reflect.Method;

/**
 * 处理连接点
 * 用于环绕通知中控制目标方法的执行
 */
public class ProceedingJoinPoint {
    private final Object target;
    private final Method method;
    private final Object[] args;

    public ProceedingJoinPoint(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    /**
     * 执行目标方法
     */
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }

    /**
     * 获取目标方法
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取目标对象
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 获取方法参数
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 获取方法签名
     */
    public String getSignature() {
        return method.toString();
    }

    /**
     * 获取目标类名
     */
    public String getTargetClass() {
        return target.getClass().getName();
    }
}