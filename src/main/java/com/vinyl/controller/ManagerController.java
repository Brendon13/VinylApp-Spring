package com.vinyl.controller;

import com.vinyl.DTO.*;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.ItemService;
import com.vinyl.service.OrderService;
import com.vinyl.service.StatusService;
import com.vinyl.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
public class ManagerController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/vinylsAdd", produces = "application/json")
    public ResponseEntity<MessageDTO> addVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        Item item = new Item();
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0){
                    messageDTO.setMessage("Quantity can't be negative!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
                else if(vinyl.getPrice() <= 0){
                    messageDTO.setMessage("Price can't be negative!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }

                else if(itemService.findByName(vinyl.getName()) == null){
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    itemService.save(item);

                    messageDTO.setMessage("Item inserted!");
                    return ResponseEntity.ok(messageDTO);
                }
                else{
                    messageDTO.setMessage("Item already exists!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
            }
            catch (NullPointerException e){
                messageDTO.setMessage("Quantity can't be null!");
                return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
            }
        }
        else{
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> deleteVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try{
                if(itemService.findById(vinyl_id).isPresent()){
                    Item item = itemService.findById(vinyl_id).get();
                    itemService.delete(item);
                    messageDTO.setMessage("Item Deleted!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.NO_CONTENT);
                }
                else{
                    messageDTO.setMessage("Item does not exist!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
                }
            }
            catch (NullPointerException e) {
                messageDTO.setMessage("Item does not exist!");
                return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
            }
        }
        else{
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/vinyls/update/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> updateVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl, @PathVariable Long vinyl_id){
        Item item = new Item();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0){
                    messageDTO.setMessage("Quantity can't be negative!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
                else if(vinyl.getPrice() <= 0){
                    messageDTO.setMessage("Price can't be negative!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
                else if(itemService.findById(vinyl_id).isPresent()){
                    item.setId(vinyl_id);
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    itemService.save(item);

                    messageDTO.setMessage("Item updated!");
                    return ResponseEntity.ok(messageDTO);
                }
                else{
                    messageDTO.setMessage("Item doesn't exists!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
            }
            catch (NullPointerException e){
                messageDTO.setMessage("Quantity can't be null!");
                return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
            }
        }
        else{
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/orders/{order_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> updateOrder(@RequestHeader("Authorization") String auth, @RequestBody StatusDTO status, @PathVariable Long order_id){
        Order order;
        Date date = new Date();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            if(orderService.findById(order_id).isPresent()){
                if(status.getId() == 1 || status.getId() == 2){
                    order = orderService.findById(order_id).get();
                    order.setStatus(statusService.findById(status.getId()));
                    order.setUpdatedAt(date);
                    orderService.save(order);

                    messageDTO.setMessage("Order status changed!");
                    return ResponseEntity.ok(messageDTO);
                }
                else{
                    messageDTO.setMessage("Status is not a valid Id!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
                }
            }
            else{
                messageDTO.setMessage("Order doesn't exist!");
                return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
            }
        }
        else{
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/customers", produces = "application/json")
    public ResponseEntity<Object> getCustomers(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            UserRole userRole = new UserRole(1L,"customer");
            List<User> users = userService.findByUserRole(userRole);
            List<UserDTO> userDTOS = new ArrayList<>();

            users.forEach(user -> {
                userDTOS.add(UserDTO.build(user));
            });

            return new ResponseEntity<>(userDTOS, HttpStatus.OK);
        }
        else {
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/users/{user_id}/orders", produces = "application/json")
    public ResponseEntity<Object> getUserOrder(@RequestHeader("Authorization") String auth, @PathVariable Long user_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if (userService.findByEmailAddress(email).getUserRole().getId() == 2) {
            if (userService.findById(user_id) == null) {
                messageDTO.setMessage("User id doesn't exist!");
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
            else {
                List<Order> orders = orderService.findByUserId(user_id);
                List<OrderDTO> orderDTOS = new ArrayList<>();

                orders.forEach(order -> {
                    orderDTOS.add(OrderDTO.build(order));
                });
                if(orderDTOS.isEmpty()){
                    messageDTO.setMessage("No orders placed!");
                    return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
                }
                else{
                    return new ResponseEntity<>(orderDTOS, HttpStatus.OK);
                }
            }
        }
        else{
            messageDTO.setMessage("You are not a manager!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public ResponseEntity<?> getVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if(userService.findByEmailAddress(email) != null && userService.findById(userService.findByEmailAddress(email).getId()) != null) {
            if(itemService.findById(vinyl_id).isPresent()){
                ItemDTO itemDTO = ItemDTO.build(itemService.findById(vinyl_id).get());
                return new ResponseEntity<>(itemDTO, HttpStatus.OK);
            }
            else
            {
                messageDTO.setMessage("Not a valid Id!");
                return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
            }
        }
        else{
            messageDTO.setMessage("You are not logged in!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }
}
