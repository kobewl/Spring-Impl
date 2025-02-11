package com.wangliang.service;

import com.spring.Component;

@Component("orderService")
public class OrderServiceImpl implements OrderService {

    @Override
    public void createOrder(String username) {
        System.out.println("Creating order for user: " + username);
    }
}