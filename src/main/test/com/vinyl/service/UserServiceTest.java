package com.vinyl.service;

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
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {
    @MockBean
    private UserService mockUserService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void findByEmailAddressTest() {
        final User user = new User();
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.email@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        Mockito.when(mockUserService.findByEmailAddress("test.email@gmail.com")).thenReturn(user);

        User testUser = mockUserService.findByEmailAddress("test.email@gmail.com");

        Assert.assertEquals(user, testUser);
        verify(mockUserService).findByEmailAddress("test.email@gmail.com");
    }

    @Test
    public void findByUserRoleTest(){
        final UserRole userRole = new UserRole(1L,"customer");


        final List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setFirstName("User");
        user1.setLastName("User");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(userRole);

        User user2 = new User();
        user2.setFirstName("User");
        user2.setLastName("User");
        user2.setEmailAddress("user.user2@gmail.com");
        user2.setPassword(bCryptPasswordEncoder.encode("123456"));
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user2.setUserRole(userRole);

        userList.add(user1);
        userList.add(user2);

        Mockito.when(mockUserService.findByUserRole(userRole)).thenReturn(userList);

        List<User> testUserList = mockUserService.findByUserRole(userRole);

        Assert.assertEquals(userList, testUserList);
        verify(mockUserService).findByUserRole(userRole);
    }

    @Test
    public void findByIdTest(){
        final UserRole userRole = new UserRole(1L,"customer");

        final User user = new User();
        user.setId(1L);
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(userRole);


        Mockito.when(mockUserService.findById(1L)).thenReturn(user);

        User testUser = mockUserService.findById(1L);

        Assert.assertEquals(user, testUser);
        verify(mockUserService).findById(1L);
    }

    @Test
    public void saveCustomerTest(){
        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        doNothing().when(mockUserService).save(isA(User.class));
        mockUserService.save(user);

        verify(mockUserService, times(1)).save(user);
    }

    @Test
    public void saveManagerTest(){
        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        doNothing().when(mockUserService).saveManager(isA(User.class));
        mockUserService.saveManager(user);

        verify(mockUserService, times(1)).saveManager(user);
    }

    @Test
    public void deleteUserTest(){
        final User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        doNothing().when(mockUserService).delete(isA(User.class));
        mockUserService.delete(user);

        verify(mockUserService, times(1)).delete(user);
    }
}
