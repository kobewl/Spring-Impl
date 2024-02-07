package com.wangliang;

import com.spring.ImplApplicationContext;

public class Test {

    public static void main(String[] args) {
        ImplApplicationContext applicationContext = new ImplApplicationContext(AppConfig.class);

        Object userService = applicationContext.getBean("userService");
    }
}
