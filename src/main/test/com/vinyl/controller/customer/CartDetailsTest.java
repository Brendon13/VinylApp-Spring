package com.vinyl.controller.customer;

import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.CustomerController;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CustomerController.class)
public class CartDetailsTest {
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
    public void getCartDetailsTestWithParamsOKAndItemsInCart() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final List<CartItem> cartItemList = new ArrayList<>();

        final Item item = new Item();
        item.setId(1L);
        item.setName("Lorem");
        item.setDescription("Lorem Ipsum");
        item.setQuantity(20L);
        item.setPrice(100D);

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

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/customer/cart/detail").header("Authorization", auth)).andDo(print()).andExpect(status().isOk());

    }

    @Test
    public void getCartDetailsTestWithParamsOKAndNoItemsInCart() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("kovacs.brendon@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        final Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        final List<CartItem> cartItemList = new ArrayList<>();

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/customer/cart/detail").header("Authorization", auth)).andDo(print()).andExpect(status().isNotFound());

    }

}
