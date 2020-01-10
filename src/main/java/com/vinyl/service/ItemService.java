package com.vinyl.service;


import com.vinyl.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    void save(Item item);
    Optional<Item> findById(Long id);
    Item findByName(String name);
    void delete(Item item);
    List<Item> findAll();
}
