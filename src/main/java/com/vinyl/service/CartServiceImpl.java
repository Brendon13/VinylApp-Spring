package com.vinyl.service;

import com.vinyl.model.Cart;
import com.vinyl.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart findByUserId(Long userId){
        return cartRepository.findByUserId(userId);
    }

    @Override
    public void save(Cart cart){
        cartRepository.save(cart);
    }

}
