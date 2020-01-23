package com.vinyl.controller;

import com.vinyl.DTO.ItemDTO;
import com.vinyl.DTO.MessageDTO;
import com.vinyl.DTO.UserRoleDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
public class CommonController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping(value = "/users", produces = "application/json")
    public ResponseEntity<MessageDTO> addUser(@Valid @RequestBody User user){
        MessageDTO messageDTO = new MessageDTO();

        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartService.save(cart);

            messageDTO.setMessage("User Created!");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } else {
            messageDTO.setMessage("Email already in use!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/managers", produces = "application/json")
    public ResponseEntity<MessageDTO> addManager(@Valid @RequestBody User user){
        MessageDTO messageDTO = new MessageDTO();

        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.saveManager(user);

            messageDTO.setMessage("Manager Created!");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } else {
            messageDTO.setMessage("Email already in use!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/users/login", produces = "application/json")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        MessageDTO messageDTO = new MessageDTO();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            messageDTO.setMessage("User credentials invalid!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping(value = "/vinyls", produces = "application/json")
    public List<ItemDTO> getVinyl(@RequestHeader("Authorization") String auth){
        List<Item> item = itemService.findAll();
        List<ItemDTO> itemDTOS = new ArrayList<>();
        item.forEach(item1 -> {
            assert false;
            itemDTOS.add(ItemDTO.build(item1));
        });
        return itemDTOS;
    }

    @DeleteMapping(value = "/users/delete" , produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> deleteUser(@RequestHeader("Authorization") String auth){
        MessageDTO messageDTO = new MessageDTO();

        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        userService.delete(userService.findById(userService.findByEmailAddress(email).getId()));

        messageDTO.setMessage("User deleted!");
        return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/verifyManager")
    public ResponseEntity<UserRoleDTO> verifyManager(@RequestHeader("Authorization") String auth){
        UserRoleDTO userRoleDTO = new UserRoleDTO();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2)
        {
            userRoleDTO.setRole(1L);
        }
        else {
            userRoleDTO.setRole(0L);
        }

        return ResponseEntity.ok(userRoleDTO);
    }
}