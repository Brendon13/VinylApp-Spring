package com.vinyl.service;

import com.vinyl.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CartItemServiceTest {
    @MockBean
    CartItemService mockCartItemService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void findByCartIdTest(){
        final List<CartItem> cartItemList = new ArrayList<>();

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final CartItem cartItem1 = new CartItem();
        cartItem1.setCart(cart);
        cartItem1.setItem(item);
        cartItem1.setQuantity(2L);

        final CartItem cartItem2 = new CartItem();
        cartItem2.setCart(cart);
        cartItem2.setItem(item);
        cartItem2.setQuantity(4L);

        cartItemList.add(cartItem1);
        cartItemList.add(cartItem2);

        Mockito.when(mockCartItemService.findByCartId(1L)).thenReturn(cartItemList);

        List<CartItem> testCartItemList = mockCartItemService.findByCartId(1L);

        Assert.assertEquals(cartItemList, testCartItemList);
        verify(mockCartItemService).findByCartId(1L);
    }

    @Test
    public void findByItemIdAndCartId(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(2L);


        Mockito.when(mockCartItemService.findByItemIdAndCartId(1L, 1L)).thenReturn(Optional.of(cartItem));

        Optional<CartItem> testCartItem = mockCartItemService.findByItemIdAndCartId(1L, 1L);

        Assert.assertEquals(Optional.of(cartItem), testCartItem);
        verify(mockCartItemService).findByItemIdAndCartId(1L,1L);
    }

    @Test
    public void deleteCartItemTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(2L);

        doNothing().when(mockCartItemService).delete(isA(CartItem.class));
        mockCartItemService.delete(cartItem);

        verify(mockCartItemService, times(1)).delete(cartItem);
    }

    @Test
    public void saveCartItemTest(){
        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(2L);

        doNothing().when(mockCartItemService).save(isA(CartItem.class));
        mockCartItemService.save(cartItem);

        verify(mockCartItemService, times(1)).save(cartItem);
    }
}
