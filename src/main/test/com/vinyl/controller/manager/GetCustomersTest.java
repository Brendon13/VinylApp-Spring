package com.vinyl.controller.manager;

import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.CustomerController;
import com.vinyl.controller.ManagerController;
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
@WebMvcTest(ManagerController.class)
public class GetCustomersTest {
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
    public void getCustomersTestWithParamsOK() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        UserRole userRole = new UserRole(1L,"customer");

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(2L, "manager"));

        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setId(2L);
        user1.setFirstName("Customer1");
        user1.setLastName("User1");
        user1.setEmailAddress("test.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(userRole);

        User user2 = new User();
        user2.setId(1L);
        user2.setFirstName("Customer2");
        user2.setLastName("User2");
        user2.setEmailAddress("test.user2@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user2.setPassword(bCryptPasswordEncoder.encode("123456"));
        user2.setUserRole(userRole);

        users.add(user1);
        users.add(user2);


        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(userService.findByUserRole(userRole)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/customers").header("Authorization", auth)).andDo(print()).andExpect(status().isOk());

    }

    @Test
    public void getCustomersTestWithParamsOkButNotManager() throws Exception {
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));
        user.setUserRole(new UserRole(1L, "customer"));

        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setId(2L);
        user1.setFirstName("Customer1");
        user1.setLastName("User1");
        user1.setEmailAddress("test.user1@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user1.setPassword(bCryptPasswordEncoder.encode("123456"));
        user1.setUserRole(new UserRole(1L, "customer"));

        User user2 = new User();
        user2.setId(1L);
        user2.setFirstName("Customer2");
        user2.setLastName("User2");
        user2.setEmailAddress("test.user2@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user2.setPassword(bCryptPasswordEncoder.encode("123456"));
        user2.setUserRole(new UserRole(1L, "customer"));

        users.add(user1);
        users.add(user2);

        UserRole userRole = new UserRole(1L,"customer");

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        String auth = "Bearer " + tokenString;

        when(jwtTokenUtil.getUsernameFromToken(tokenString)).thenReturn("test.user@gmail.com");
        when(userService.findByEmailAddress("test.user@gmail.com")).thenReturn(user);
        when(userService.findByUserRole(userRole)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/VinylStore/api/customers").header("Authorization", auth)).andDo(print()).andExpect(status().isForbidden());

    }
}
