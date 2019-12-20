package com.vinyl.service;

import com.vinyl.model.Order;

import java.util.List;

public interface OrderService {
    void save(Order order);
    Order findById(Long id);
    List<Order> findByUserId(Long id);
}
