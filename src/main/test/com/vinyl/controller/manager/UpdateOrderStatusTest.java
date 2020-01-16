package com.vinyl.controller.manager;

import com.google.gson.JsonObject;
import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.ManagerController;
import com.vinyl.model.*;
import com.vinyl.repository.UserRepository;
import com.vinyl.service.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ManagerController.class)
public class UpdateOrderStatusTest {

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
    public void updateOrderStatusTestWithParamsOK() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
        JSONObject json = new JSONObject();

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
        final Status status2 = new Status(2L,"delivered");

        final Date date = new Date();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(orderService.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(statusService.findById(2L)).thenReturn(status2);

        doNothing().when(orderService).save(isA(Order.class));
        orderService.save(order);

        verify(orderService, times(1)).save(order);

        json.put("id", 1);

        mockMvc.perform(MockMvcRequestBuilders.put("/VinylStore/api/orders/{order_id}", "1")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json.toString()))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void updateOrderStatusTestWithNotValidStatusId() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
        JSONObject json = new JSONObject();

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
        final Status status2 = new Status(2L,"delivered");

        final Date date = new Date();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(orderService.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(statusService.findById(2L)).thenReturn(status2);

        doNothing().when(orderService).save(isA(Order.class));
        orderService.save(order);

        verify(orderService, times(1)).save(order);

        json.put("id", 3);

        mockMvc.perform(MockMvcRequestBuilders.put("/VinylStore/api/orders/{order_id}", "1")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json.toString()))
                .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    public void updateOrderStatusTestWithParamsOkButNotManager() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
        JSONObject json = new JSONObject();

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        final User user1 = new User();
        user1.setFirstName("User");
        user1.setLastName("User");
        user1.setEmailAddress("user.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(new UserRole(1L, "customer"));

        final Status status = new Status(1L,"active");
        final Status status2 = new Status(2L,"delivered");

        final Date date = new Date();

        final Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setUser(user1);
        order.setTotal_price(250D);
        order.setCreatedAt(date);
        order.setUpdatedAt(date);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(orderService.findById(1L)).thenReturn(java.util.Optional.of(order));
        when(statusService.findById(2L)).thenReturn(status2);

        doNothing().when(orderService).save(isA(Order.class));
        orderService.save(order);

        verify(orderService, times(1)).save(order);

        json.put("id", 1);

        mockMvc.perform(MockMvcRequestBuilders.put("/VinylStore/api/orders/{order_id}", "1")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json.toString()))
                .andDo(print()).andExpect(status().isForbidden());
    }

}
