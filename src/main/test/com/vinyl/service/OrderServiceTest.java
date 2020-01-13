package com.vinyl.service;

import com.vinyl.model.Order;
import com.vinyl.model.Status;
import com.vinyl.model.User;
import com.vinyl.model.UserRole;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class OrderServiceTest {
    @MockBean
    private OrderService mockOrderService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void saveOrderTest(){
        final Status status = new Status(1L,"active");

        final UserRole userRole = new UserRole(1L,"customer");

        final User user = new User();
        user.setId(1L);
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(userRole);

        final Date date = new Date();

        final Order order = new Order();
        order.setStatus(status);
        order.setUser(user);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        doNothing().when(mockOrderService).save(isA(Order.class));
        mockOrderService.save(order);

        verify(mockOrderService, times(1)).save(order);
    }

    @Test
    public void findByIdTest(){
        final Status status = new Status(1L,"active");

        final UserRole userRole = new UserRole(1L,"customer");

        final User user = new User();
        user.setId(1L);
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(userRole);

        final Date date = new Date();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);


        Mockito.when(mockOrderService.findById(1L)).thenReturn(java.util.Optional.of(order));

        Order testOrder = mockOrderService.findById(1L).get();

        Assert.assertEquals(order, testOrder);
        verify(mockOrderService).findById(1L);
    }

    @Test
    public void findByUserIdTest(){
        final Status status = new Status(1L,"active");

        final UserRole userRole = new UserRole(1L,"customer");

        final User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("User1");
        user1.setLastName("User1");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(userRole);

        final Date date = new Date();

        final List<Order> orderList = new ArrayList<>();

        final Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(status);
        order1.setUser(user1);
        order1.setTotal_price(250D);
        order1.setCreatedAt(date);
        order1.setUpdatedAt(date);

        final Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(status);
        order2.setUser(user1);
        order2.setTotal_price(250D);
        order2.setCreatedAt(date);
        order2.setUpdatedAt(date);

        orderList.add(order1);
        orderList.add(order2);

        Mockito.when(mockOrderService.findByUserId(1L)).thenReturn(orderList);

        List<Order> testOrderList = mockOrderService.findByUserId(1L);

        Assert.assertEquals(orderList, testOrderList);
        verify(mockOrderService).findByUserId(1L);

    }
}
