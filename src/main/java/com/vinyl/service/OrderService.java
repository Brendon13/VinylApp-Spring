package com.vinyl.service;

import com.vinyl.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    void save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByUserId(Long id);
}
