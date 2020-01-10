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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CustomerController.class)
public class AddVinylToCartTest {

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
    public void addVinylToCartTestWithQuantityOK() throws Exception {
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

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(1L);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        assertTrue(itemService.findById(1L).isPresent());
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);
       // when(cartItemService.findByItemIdAndCartId(1L, cart.getId()).getId()).thenReturn(1L);


        doNothing().when(cartItemService).save(isA(CartItem.class));
        cartItemService.save(cartItem);

        verify(cartItemService, times(1)).save(cartItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls/cart/{vinyl_id}", "1").header("Authorization", auth).param("quantity", "1")).andDo(print()).andExpect(status().isOk());

    }

    @Test
    public void addVinylToCartTestWithQuantityTooMuch() throws Exception {
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

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(1L);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(itemService.findById(1L).get()).thenReturn(item);
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);


        doNothing().when(cartItemService).save(isA(CartItem.class));
        cartItemService.save(cartItem);

        verify(cartItemService, times(1)).save(cartItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls/cart/{vinyl_id}", "1").header("Authorization", auth).param("quantity", "100")).andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void addVinylToCartTestWithQuantityZero() throws Exception {
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

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(1L);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(itemService.findById(1L).get()).thenReturn(item);
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);


        doNothing().when(cartItemService).save(isA(CartItem.class));
        cartItemService.save(cartItem);

        verify(cartItemService, times(1)).save(cartItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls/cart/{vinyl_id}", "1").header("Authorization", auth).param("quantity", "0")).andDo(print()).andExpect(status().isForbidden());

    }
    @Test
    public void addVinylToCartTestWithQuantityNegative() throws Exception {
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

        final CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setQuantity(1L);

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("kovacs.brendon@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("kovacs.brendon@gmail.com");
        when(userService.findByEmailAddress("kovacs.brendon@gmail.com")).thenReturn(user);
        when(cartService.findByUserId(111L)).thenReturn(cart);
        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(cartItemService.findByCartId(1L)).thenReturn(cartItemList);


        doNothing().when(cartItemService).save(isA(CartItem.class));
        cartItemService.save(cartItem);

        verify(cartItemService, times(1)).save(cartItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/VinylStore/api/vinyls/cart/{vinyl_id}", "1").header("Authorization", auth).param("quantity", "-10")).andDo(print()).andExpect(status().isForbidden());

    }
}
