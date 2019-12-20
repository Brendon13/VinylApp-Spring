package com.vinyl.controller.manager;

import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.ManagerController;
import com.vinyl.model.Order;
import com.vinyl.model.Status;
import com.vinyl.model.User;
import com.vinyl.model.UserRole;
import com.vinyl.repository.UserRepository;
import com.vinyl.service.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ManagerController.class)
public class GetOrderFromUserTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

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
    private CartItemService cartItemService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private StatusService statusService;

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
    public void getOrderFromUserTestWithParamsOK() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final User user1 = new User();
        user1.setFirstName("User");
        user1.setLastName("User");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(new UserRole(1L, "customer"));

        final Status status = new Status(1L,"active");

        final Date date = new Date();

        final List<Order> orders = new ArrayList<>();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        final Order order1 = new Order();
        order1.setId(2L);
        order1.setStatus(status);
        order1.setUser(user1);
        order1.setTotal_price(300D);
        order1.setCreatedAt(date);
        order1.setUpdatedAt(date);

        orders.add(order);
        orders.add(order1);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(userService.findById(111L)).thenReturn(user1);
        when(orderService.findByUserId(111L)).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/users/{user_id}/orders", "111").header("Authorization", auth)).andDo(print()).andExpect(status().isOk());

    }

    @Test
    public void getOrderFromUserTestWithNotExistingUserId() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("User");
        user1.setLastName("User");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(new UserRole(1L, "customer"));

        final Status status = new Status(1L,"active");

        final Date date = new Date();

        final List<Order> orders = new ArrayList<>();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        final Order order1 = new Order();
        order1.setId(2L);
        order1.setStatus(status);
        order1.setUser(user1);
        order1.setTotal_price(300D);
        order1.setCreatedAt(date);
        order1.setUpdatedAt(date);

        orders.add(order);
        orders.add(order1);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(userService.findById(111L)).thenReturn(null);
        when(orderService.findByUserId(111L)).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/users/{user_id}/orders", "111").header("Authorization", auth)).andDo(print()).andExpect(status().isBadRequest());

    }
    @Test
    public void getOrderFromUserTestWithParamsOkButNotManager() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        final User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("User");
        user1.setLastName("User");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(new UserRole(1L, "customer"));

        final Status status = new Status(1L,"active");

        final Date date = new Date();

        final List<Order> orders = new ArrayList<>();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        final Order order1 = new Order();
        order1.setId(2L);
        order1.setStatus(status);
        order1.setUser(user1);
        order1.setTotal_price(300D);
        order1.setCreatedAt(date);
        order1.setUpdatedAt(date);

        orders.add(order);
        orders.add(order1);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(userService.findById(111L)).thenReturn(user1);
        when(orderService.findByUserId(111L)).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/users/{user_id}/orders", "111").header("Authorization", auth)).andDo(print()).andExpect(status().isForbidden());

    }



}
