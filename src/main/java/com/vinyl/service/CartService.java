package com.vinyl.service;

import com.vinyl.model.Cart;

public interface CartService {
    Cart findByUserId(Long userId);
    void save(Cart cart);
}
