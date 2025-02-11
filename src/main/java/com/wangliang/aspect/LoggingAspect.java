package com.wangliang.aspect;

import com.spring.Component;
import com.spring.aop.*;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.wangliang.service.*.*(..))")
    public void logBefore() {
        System.out.println("=== Method execution start ===");
    }

    @Before("execution(* com.wangliang.service.UserService.*(..))")
    public void logUserService() {
        System.out.println("=== UserService method called ===");
    }
}