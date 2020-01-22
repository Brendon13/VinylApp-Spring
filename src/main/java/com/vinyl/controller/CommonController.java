package com.vinyl.controller;

import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
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
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user, BindingResult result) throws JSONException {
        JSONObject json = new JSONObject();
        if(result.hasErrors()) {
            json.put("Message ", errorResponse(result));
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartService.save(cart);

            json.put("Message ", "User Created!");
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } else {
            json.put("Message ", "Email already in use!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/managers", produces = "application/json")
    public ResponseEntity<Object> addManager(@Valid @RequestBody User user, BindingResult result) throws JSONException {
        JSONObject json = new JSONObject();
        if(result.hasErrors()) {
            json.put("Message ", errorResponse(result));
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.saveManager(user);
            json.put("Message ", "Manager Created!");
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } else {
            json.put("Message ", "Email already in use!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/users/login", produces = "application/json")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest, BindingResult result) throws Exception {
        JSONObject json = new JSONObject();
        if(result.hasErrors()) {
            return new ResponseEntity<>(errorResponse(result), HttpStatus.BAD_REQUEST);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            json.put("Message", "User Credentials Invalid!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping(value = "/vinyls", produces = "application/json")
    public ResponseEntity<?> getVinyl(@RequestHeader("Authorization") String auth) throws JSONException{
        JSONObject jsonErr = new JSONObject();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email) != null && userService.findById(userService.findByEmailAddress(email).getId()) != null) {

            List<Item> items = itemService.findAll();

            JSONObject json = new JSONObject();
            JSONArray json3 = new JSONArray();

            for(int i = 0; i< (long) items.size(); i++){
                JSONObject json2 = new JSONObject();
                json2.put("Id", items.get(i).getId());
                json2.put("Name", items.get(i).getName());
                json2.put("Description", items.get(i).getDescription());
                json2.put("Price", items.get(i).getPrice());
                json2.put("Quantity", items.get(i).getQuantity());
                json3.put(json2);
            }

            json.put("Vinyls", json3);

            return new ResponseEntity<>(json3.toString(), HttpStatus.OK); //json.toString() to display Vinyls:
        }
        else{
            jsonErr.put("Message ", "You are not logged in!");
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/users/delete" , produces = "application/json")
    public @ResponseBody ResponseEntity<Object> deleteUser(@RequestHeader("Authorization") String auth) throws JSONException {
        JSONObject json = new JSONObject();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        userService.delete(userService.findById(userService.findByEmailAddress(email).getId()));
        json.put("Message ", "User Deleted!");
        return ResponseEntity.ok(json.toString());

    }

    @GetMapping(value = "/verifyManager")
    public ResponseEntity<?> verifyManager(@RequestHeader("Authorization") String auth) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        json.put("role", userService.findByEmailAddress(email).getUserRole().getId() == 2);
        return ResponseEntity.ok(json.toString());
    }

    private ArrayList<String> errorResponse(BindingResult result){
        ArrayList<String> errorMessage = new ArrayList<>();
        result.getFieldErrors().forEach(eM -> errorMessage.add(eM.getDefaultMessage()));
        return errorMessage;
    }
}