package com.vinyl.controller;

import com.vinyl.DTO.ItemDTO;
import com.vinyl.DTO.MessageDTO;
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
    private ItemService itemService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping(value = "/users/login", produces = "application/json")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            MessageResponse("User credentials invalid!", HttpStatus.OK);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping(value = "/vinyls", produces = "application/json")
    public List<ItemDTO> getVinyl(@RequestHeader("Authorization") String auth){
        List<Item> item = itemService.findAll();
        List<ItemDTO> itemDTOS = new ArrayList<>();
        item.forEach(item1 -> itemDTOS.add(ItemDTO.build(item1)));
        return itemDTOS;
    }

    @DeleteMapping(value = "/users/delete" , produces = "application/json")
    public @ResponseBody
    void deleteUser(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        userService.delete(userService.findById(userService.findByEmailAddress(email).getId()));

        MessageResponse("User deleted!", HttpStatus.OK);
    }

    public ResponseEntity<MessageDTO> MessageResponse(String message, HttpStatus httpStatus){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        return new ResponseEntity<>(messageDTO, httpStatus);
    }
}