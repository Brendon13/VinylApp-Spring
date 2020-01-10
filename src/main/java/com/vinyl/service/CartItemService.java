package com.vinyl.service;

import com.vinyl.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemService {
    List<CartItem> findByCartId(Long cartId);
    CartItem findByItemId(Long itemId);
    Optional<CartItem> findByItemIdAndCartId(Long itemId, Long cartId);
    void save(CartItem cartItem);
    void delete(CartItem cartItem);
}
