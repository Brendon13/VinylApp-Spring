package com.vinyl.service;

import com.vinyl.exception.ItemNotFoundException;
import com.vinyl.model.Item;
import com.vinyl.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void save(Item item){
        itemRepository.save(item);
    }

    @Override
    public Optional<Item> findById(Long id){
        return Optional.ofNullable(itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }

    @Override
    public Item findByName(String name){
        return itemRepository.findByName(name);
    }

    @Override
    public void delete(Item item) {itemRepository.delete(item);}

    @Override
    public List<Item> findAll(){
        return itemRepository.findAll();
    }
}
