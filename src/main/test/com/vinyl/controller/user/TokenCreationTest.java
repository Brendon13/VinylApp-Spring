package com.vinyl.controller.user;

import com.vinyl.config.JwtAuthenticationEntryPoint;
import com.vinyl.config.JwtRequestFilter;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.controller.UserController;
import com.vinyl.model.User;
import com.vinyl.repository.UserRepository;
import com.vinyl.service.CartService;
import com.vinyl.service.ItemService;
import com.vinyl.service.JwtUserDetailsService;
import com.vinyl.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserController.class)
@WebAppConfiguration
@ContextConfiguration
public class TokenCreationTest {
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(jwtRequestFilter).build();
    }

    @Test
    public void userLoginTestWithParametersOk() throws Exception{
        userLogin("test.user@gmail.com", "123456").andExpect(status().isOk());
    }

    @Test
    public void userLoginTestWithNoUsername() throws Exception{
        userLogin("", "123456").andExpect(status().isBadRequest());
    }

    @Test
    public void userLoginTestWithNoPassword() throws Exception{
        userLogin("test.user@gmail.com", "").andExpect(status().isBadRequest());
    }

    @Test
    public void tokenGenerationTestWithParametersOK() throws Exception{
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test.user@gmail.com");

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        when(userDetailsService.loadUserByUsername("test.user@gmail.com")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(tokenString);

        userLogin(user.getEmailAddress(), user.getPassword()).andDo(print()).andExpect(status().isOk());
    }
    @Test
    public void tokenGenerationTestWithNoEmailAddress() throws Exception{
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test.user@gmail.com");

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        when(userDetailsService.loadUserByUsername("test.user@gmail.com")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(tokenString);

        userLogin("", user.getPassword()).andDo(print()).andExpect(status().isBadRequest());
    }
    @Test
    public void tokenGenerationTestWithNoPassword() throws Exception{
        final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

        User user = new User();
        user.setId(111L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setEmailAddress("test.user@gmail.com");
        when(bCryptPasswordEncoder.encode("123456")).thenReturn("$2a$10$GVTnofdX9dK/1xZXRv3hNuGy2Jw1mV56/cl2untyOlqYdRoVYB2X2");
        user.setPassword(bCryptPasswordEncoder.encode("123456"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test.user@gmail.com");

        Map<String, Object> claims = new HashMap<>();
        String tokenString = Jwts.builder().setClaims(claims).setSubject("test.user@gmail.com").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, "vinylapp").compact();

        assertNotNull(tokenString);

        when(userDetailsService.loadUserByUsername("test.user@gmail.com")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(tokenString);

        userLogin(user.getEmailAddress(), "").andDo(print()).andExpect(status().isBadRequest());
    }




    private ResultActions userLogin(String username, String password) throws Exception {
        return mockMvc.perform(
                post("/VinylStore/api/users/login")
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                        .contentType("application/json")
                        .characterEncoding("utf-8"));
    }
}
