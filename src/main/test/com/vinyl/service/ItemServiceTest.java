package com.vinyl.service;

import com.vinyl.model.Item;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ItemServiceTest {

    @MockBean
    private ItemService mockItemService;

    @Test
    public void saveItemTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        doNothing().when(mockItemService).save(isA(Item.class));
        mockItemService.save(item);

        verify(mockItemService, times(1)).save(item);
    }

    @Test
    public void findByIdTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        Mockito.when(mockItemService.findById(1L)).thenReturn(java.util.Optional.of(item));

        Item testItem = mockItemService.findById(1L).get();

        Assert.assertEquals(item, testItem);
        verify(mockItemService).findById(1L);
    }

    @Test
    public void findByNameTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        Mockito.when(mockItemService.findByName("Lorem")).thenReturn(item);

        Item testItem = mockItemService.findByName("Lorem");

        Assert.assertEquals(item, testItem);
        verify(mockItemService).findByName("Lorem");
    }

    @Test
    public void findAllTest(){
        final List<Item> itemList = new ArrayList<>();

        final Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Lorem");
        item1.setDescription("Lorem Ipsum");
        item1.setQuantity(20L);
        item1.setPrice(100D);

        final Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Lorem2");
        item2.setDescription("Lorem Ipsum2");
        item2.setQuantity(22L);
        item2.setPrice(120D);

        itemList.add(item1);
        itemList.add(item2);

        Mockito.when(mockItemService.findAll()).thenReturn(itemList);

        List<Item> testItemList = mockItemService.findAll();

        Assert.assertEquals(itemList, testItemList);
        verify(mockItemService).findAll();
    }

    @Test
    public void deleteItemTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        doNothing().when(mockItemService).delete(isA(Item.class));
        mockItemService.delete(item);

        verify(mockItemService, times(1)).delete(item);
    }
}
