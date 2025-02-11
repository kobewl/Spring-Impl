package com.wangliang.service;

import com.spring.Component;
import com.spring.InitializingBean;
import com.spring.Autowired;

@Component("userService")
public class UserService implements InitializingBean {

    @Autowired
    private OrderService orderService;

    public void createUser(String username) {
        System.out.println("Creating user: " + username);
        // 调用订单服务
        orderService.createOrder(username);
    }

    public void deleteUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        System.out.println("Deleting user: " + username);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("UserService initialized!");
    }
}
