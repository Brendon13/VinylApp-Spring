package com.vinyl.controller.item;

import com.google.gson.Gson;
import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.ItemController;
import com.vinyl.model.*;
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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ItemController.class)
public class AddVinylToStoreTest {

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
    public void addVinylToStoreTestWithParamsOK() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(null);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isOk());

    }

    @Test
    public void addVinylToStoreTestWithItemExist() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(item);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void addVinylToStoreTestWithQuantityNegative() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(-2L);
        item.setPrice(100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(null);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void addVinylToStoreTestWithQuantityNull() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(null);
        item.setPrice(100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(null);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void addVinylToStoreTestWithPriceNegative() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(-100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(null);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void addVinylToStoreTestWithParamsOkButNotManager() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

        Gson gson = new Gson();
        String json = gson.toJson(item);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(itemService.findByName("Lorem")).thenReturn(null);

        doNothing().when(itemService).save(isA(Item.class));
        itemService.save(item);

        verify(itemService, times(1)).save(item);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls")
                .header("Authorization", auth)
                .contentType("application/json")
                .characterEncoding("utf-8")
                .content(json)).andDo(print()).andExpect(status().isForbidden());

    }
}
