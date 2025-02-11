package com.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * AOP 代理创建器
 * 用于创建代理对象并处理通知的调用
 */
public class AopProxy implements InvocationHandler {

    private final Object target;
    private final List<AspectInfo> aspects;

    public AopProxy(Object target) {
        this.target = target;
        this.aspects = new ArrayList<>();
    }

    /**
     * 添加切面信息
     */
    public void addAspect(Object aspect, Method adviceMethod, String pointcut, AdviceType type) {
        aspects.add(new AspectInfo(aspect, adviceMethod, pointcut, type));
    }

    /**
     * 创建代理对象
     */
    public Object createProxy() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果存在环绕通知，优先处理
        for (AspectInfo aspect : aspects) {
            if (aspect.type == AdviceType.AROUND && PointcutParser.matches(aspect.pointcut, method)) {
                return aspect.adviceMethod.invoke(aspect.aspect,
                        new ProceedingJoinPoint(target, method, args));
            }
        }

        // 执行前置通知
        for (AspectInfo aspect : aspects) {
            if (aspect.type == AdviceType.BEFORE && PointcutParser.matches(aspect.pointcut, method)) {
                aspect.adviceMethod.invoke(aspect.aspect);
            }
        }

        Object result = null;
        Throwable throwable = null;

        try {
            // 执行目标方法
            result = method.invoke(target, args);
        } catch (Throwable t) {
            throwable = t;
            // 执行异常通知
            for (AspectInfo aspect : aspects) {
                if (aspect.type == AdviceType.AFTER_THROWING &&
                        PointcutParser.matches(aspect.pointcut, method)) {
                    aspect.adviceMethod.invoke(aspect.aspect, t);
                }
            }
            throw t;
        } finally {
            // 执行后置通知
            for (AspectInfo aspect : aspects) {
                if (aspect.type == AdviceType.AFTER && PointcutParser.matches(aspect.pointcut, method)) {
                    aspect.adviceMethod.invoke(aspect.aspect);
                }
            }
        }

        return result;
    }

    /**
     * 切面信息类
     */
    private static class AspectInfo {
        final Object aspect;
        final Method adviceMethod;
        final String pointcut;
        final AdviceType type;

        AspectInfo(Object aspect, Method adviceMethod, String pointcut, AdviceType type) {
            this.aspect = aspect;
            this.adviceMethod = adviceMethod;
            this.pointcut = pointcut;
            this.type = type;
        }
    }

    /**
     * 通知类型
     */
    public enum AdviceType {
        BEFORE,
        AFTER,
        AROUND,
        AFTER_THROWING
    }
}