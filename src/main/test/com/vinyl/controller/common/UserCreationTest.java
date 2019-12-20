package com.vinyl.controller.common;

import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.CommonController;
import com.vinyl.model.User;
import com.vinyl.service.CartService;
import com.vinyl.service.ItemService;
import com.vinyl.service.JwtUserDetailsService;
import com.vinyl.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CommonController.class)
public class UserCreationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private CartService cartService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(jwtRequestFilter).build();
    }

    @Test
    public void customerCreationTestWithParametersOK() throws Exception{
        registerCustomer("Ion", "Ionescu", "ion.ionescu@gmail.com", "123456").andExpect(status().isOk());
    }

    @Test
    public void customerCreationTestWithNoFirstName() throws Exception{
        registerCustomer("", "Ionescu", "ion.ionescu@gmail.com", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void customerCreationTestWithNoLastName() throws Exception{
        registerCustomer("Ion", "", "ion.ionescu@gmail.com", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void customerCreationTestWithNoEmailAddress() throws Exception{
        registerCustomer("Ion", "Ionescu", "", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void customerCreationTestWithNotEmailAddressFormat() throws Exception{
        registerCustomer("Ion", "Ionescu", "ion.ionescu", "123456").andExpect(status().is4xxClientError());
    }


    @Test
    public void customerCreationTestWitEmailAddressAlreadyUsed() throws Exception{
        User user = new User();
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("customer.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        Mockito.when(userService.findByEmailAddress("customer.user@gmail.com")).thenReturn(user);

        registerCustomer("Ion", "Ionescu", "customer.user@gmail.com", "123456").andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    public void customerCreationTestWithNoPassword() throws Exception{
        registerCustomer("Ion", "Ionescu", "ion.ionescu", "").andExpect(status().is4xxClientError());
    }

    @Test
    public void managerCreationTestWithParametersOK() throws Exception{
        registerManager("Ion", "Ionescu", "ion.ionescu@gmail.com", "123456").andExpect(status().isOk());
    }

    @Test
    public void managerCreationTestWithNoFirstName() throws Exception{
        registerManager("", "Ionescu", "ion.ionescu@gmail.com", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void managerCreationTestWithNoLastName() throws Exception{
        registerManager("Ion", "", "ion.ionescu@gmail.com", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void managerCreationTestWithNoEmailAddress() throws Exception{
        registerManager("Ion", "Ionescu", "", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void managerCreationTestWithNotEmailAddressFormat() throws Exception{
        registerManager("Ion", "Ionescu", "ion.ionescu", "123456").andExpect(status().is4xxClientError());
    }

    @Test
    public void managerCreationTestWitEmailAddressAlreadyUsed() throws Exception{
        User user = new User();
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("store.manager@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        Mockito.when(userService.findByEmailAddress("store.manager@gmail.com")).thenReturn(user);

        registerManager("Ion", "Ionescu", "store.manager@gmail.com", "123456").andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    public void managerCreationTestWithNoPassword() throws Exception{
        registerManager("Ion", "Ionescu", "ion.ionescu", "").andExpect(status().is4xxClientError());
    }

    private ResultActions registerCustomer(String firstName, String lastName, String emailAddress, String password) throws Exception {
        return mockMvc.perform(
                post("/VinylStore/api/users")
                        .content("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\", \"emailAddress\":\"" + emailAddress + "\", \"password\":\""
                                + password + "\"}")
                        .contentType("application/json")
                        .characterEncoding("utf-8"));
    }

    private ResultActions registerManager(String firstName, String lastName, String emailAddress, String password) throws Exception {
        return mockMvc.perform(
                post("/VinylStore/api/managers")
                        .content("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\", \"emailAddress\":\"" + emailAddress + "\", \"password\":\""
                                + password + "\"}")
                        .contentType("application/json")
                        .characterEncoding("utf-8"));
    }
}
