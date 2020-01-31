package com.vinyl.controller;

import com.vinyl.DTO.MessageDTO;
import com.vinyl.DTO.UserDTO;
import com.vinyl.DTO.UserRoleDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.CartService;
import com.vinyl.service.JwtUserDetailsService;
import com.vinyl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping(value = "/users/customer", produces = "application/json")
    public ResponseEntity<MessageDTO> addUser(@Valid @RequestBody User user){
        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartService.save(cart);

            return MessageResponse("User Created!", HttpStatus.OK);
        }
        else {
            return MessageResponse("Email already in use!", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/users/manager", produces = "application/json")
    public ResponseEntity<MessageDTO> addManager(@Valid @RequestBody User user, @RequestHeader("Authorization") String auth){
        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.saveManager(user);

            return MessageResponse("Manager Created!", HttpStatus.OK);
        }
        else {
            return MessageResponse("Email already in use!", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/users/login", produces = "application/json")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @DeleteMapping(value = "/users/delete" , produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> deleteUser(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        userService.delete(userService.findById(userService.findByEmailAddress(email).getId()));

        return MessageResponse("User deleted!", HttpStatus.OK);
    }

    @GetMapping(value = "/users/customers", produces = "application/json")
    public ResponseEntity<?> getCustomers(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        //if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            UserRole userRole = new UserRole(1L,"customer");
            List<User> users = userService.findByUserRole(userRole);
            List<UserDTO> userDTOS = new ArrayList<>();

            users.forEach(user -> {
                userDTOS.add(UserDTO.build(user));
            });

            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
//        }
//        else {
//            return MessageResponse("You are not a manager!", HttpStatus.FORBIDDEN);
//        }
    }

    @GetMapping(value = "/users/role")
    public ResponseEntity<UserRoleDTO> verifyManager(@RequestHeader("Authorization") String auth){
        UserRoleDTO userRoleDTO = new UserRoleDTO();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2) {
            userRoleDTO.setRole(1L);
        }
        else {
            userRoleDTO.setRole(0L);
        }
        return ResponseEntity.ok(userRoleDTO);
    }

    public ResponseEntity<MessageDTO> MessageResponse(String message, HttpStatus httpStatus){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        return new ResponseEntity<>(messageDTO, httpStatus);
    }
}