package com.vinyl.service;

import com.vinyl.model.Cart;
import com.vinyl.model.User;
import com.vinyl.model.UserRole;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CartServiceTest {

    @MockBean
    private CartService mockCartService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void findByUserIdTest(){
        final UserRole userRole = new UserRole(1L,"customer");

        final User user = new User();
        user.setId(1L);
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(userRole);

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        Mockito.when(mockCartService.findByUserId(1L)).thenReturn(cart);

        Cart testCart = mockCartService.findByUserId(1L);

        Assert.assertEquals(cart, testCart);
        verify(mockCartService).findByUserId(1L);
    }

    @Test
    public void saveCartTest(){
        final UserRole userRole = new UserRole(1L,"customer");

        final User user = new User();
        user.setId(1L);
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(userRole);

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        doNothing().when(mockCartService).save(isA(Cart.class));
        mockCartService.save(cart);

        verify(mockCartService, times(1)).save(cart);
    }
}
