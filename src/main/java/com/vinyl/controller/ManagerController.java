package com.vinyl.controller;

import com.vinyl.DTO.StatusDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.ItemService;
import com.vinyl.service.OrderService;
import com.vinyl.service.StatusService;
import com.vinyl.service.UserService;
import io.swagger.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
@Api(value="ManagerController")
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
    public ResponseEntity<?> addVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        Item item = new Item();
        JSONObject json = new JSONObject();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0){
                    json.put("Message", "Quantity can't be negative!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
                else if(vinyl.getPrice() <= 0){
                    json.put("Message", "Price can't be negative!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }

                else if(itemService.findByName(vinyl.getName()) == null){
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    itemService.save(item);

                    json.put("Message", "Item inserted!");
                    return ResponseEntity.ok(json.toString());
                }
                else{
                    json.put("Message", "Item already exists!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
            }
            catch (NullPointerException | JSONException e){
                json.put("Message", "Quantity can't be null!");
                return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
            }
        }
        else{
            json.put("Message", "You are not a manager!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> deleteVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try{
                if(itemService.findById(vinyl_id).isPresent()){
                    Item item = itemService.findById(vinyl_id).get();
                    itemService.delete(item);
                    json.put("Message", "Item Deleted!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.NO_CONTENT);
                }
                else{
                    json.put("Message", "Item does not exist!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.NOT_FOUND);
                }
            }
            catch (NullPointerException e) {
                json.put("Message", "Item does not exist!");
                return new ResponseEntity<>(json.toString(), HttpStatus.NOT_FOUND);
            }

        }
        else{
            json.put("Message", "You are not a manager!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/vinyls/update/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> updateVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl, @PathVariable Long vinyl_id) throws JSONException {
        Item item = new Item();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0){
                    json.put("Message", "Quantity can't be negative!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
                else if(vinyl.getPrice() <= 0){
                    json.put("Message", "Price can't be negative!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
                else if(itemService.findById(vinyl_id).isPresent()){
                    item.setId(vinyl_id);
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    itemService.save(item);
                    json.put("Message", "Item updated!");
                    return ResponseEntity.ok(json.toString());
                }
                else{
                    json.put("Message", "Item doesn't exists!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
            }
            catch (NullPointerException | JSONException e){
                json.put("Message", "Quantity can't be null!");
                return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
            }
        }
        else{
            json.put("Message", "You are not a manager!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/orders/{order_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> updateOrder(@RequestHeader("Authorization") String auth, @RequestBody StatusDTO status, @PathVariable Long order_id) throws JSONException {
        Order order;
        Date date = new Date();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            if(orderService.findById(order_id).isPresent()){
                if(status.getId() == 1 || status.getId() == 2){
                    order = orderService.findById(order_id).get();
                    order.setStatus(statusService.findById(status.getId()));
                    order.setUpdatedAt(date);
                    orderService.save(order);

                    json.put("Message", "Order status changed!");
                    return ResponseEntity.ok(json.toString());
                }
                else{
                    json.put("Message", "Status is not a valid Id!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
            }
            else{
                json.put("Message", "Order doesn't exist!");
                return new ResponseEntity<>(json.toString(), HttpStatus.NOT_FOUND);
            }
        }
        else{
            json.put("Message", "You are not a manager!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/customers", produces = "application/json")
    public ResponseEntity<?> getCustomers(@RequestHeader("Authorization") String auth) throws JSONException{
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            UserRole userRole = new UserRole(1L,"customer");
            List<User> users = userService.findByUserRole(userRole);

            JSONArray json3 = new JSONArray();

            for(int i = 0; i< (long) users.size(); i++){
                JSONObject json2 = new JSONObject();
                json2.put("Id", users.get(i).getId());
                json2.put("Email", users.get(i).getEmailAddress());
                json2.put("FirstName", users.get(i).getFirstName());
                json2.put("LastName", users.get(i).getLastName());
                json3.put(json2);
            }

            return new ResponseEntity<>(json3.toString(), HttpStatus.OK);
        }
        else {
            json.put("Message", "You are not a manager!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/users/{user_id}/orders", produces = "application/json")
    public ResponseEntity<?> getUserOrder(@RequestHeader("Authorization") String auth, @PathVariable Long user_id) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject jsonError = new JSONObject();
            if (userService.findByEmailAddress(email).getUserRole().getId() == 2) {
                    if (userService.findById(user_id) == null) {
                        jsonError.put("Message", "User id doesn't exist!");
                        return new ResponseEntity<>(jsonError.toString(), HttpStatus.BAD_REQUEST);
                    } else {

                        List<Order> orders = orderService.findByUserId(user_id);
                        JSONArray json3 = new JSONArray();

                        for (int i = 0; i < (long) orders.size(); i++) {
                            JSONObject json2 = new JSONObject();
                            json2.put("Id", orders.get(i).getId());
                            json2.put("Cost", orders.get(i).getTotal_price());
                            json2.put("OrderDate", orders.get(i).getCreatedAt());
                            json2.put("UpdateDate", orders.get(i).getUpdatedAt());
                            json2.put("Status", orders.get(i).getStatus().getStatus());
                            json3.put(json2);
                        }

                        return new ResponseEntity<>(json3.toString(), HttpStatus.OK);
                    }
            } else{
                jsonError.put("Message", "You are not a manager!");
                return new ResponseEntity<>(jsonError.toString(), HttpStatus.FORBIDDEN);
            }

    }

    @GetMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public ResponseEntity<?> getVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id) throws JSONException{
        JSONObject jsonErr = new JSONObject();
        JSONObject json = new JSONObject();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email) != null && userService.findById(userService.findByEmailAddress(email).getId()) != null) {
            if(itemService.findById(vinyl_id).isPresent()){
                Item item = itemService.findById(vinyl_id).get();
                json.put("Id", item.getId());
                json.put("Name", item.getName());
                json.put("Description", item.getDescription());
                json.put("Price", item.getPrice());
                json.put("Quantity", item.getQuantity());

                return new ResponseEntity<>(json.toString(), HttpStatus.OK);
            }
            else
            {
                jsonErr.put("Message ", "Not a valid id!");
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.FORBIDDEN);
            }
        }
        else{
            jsonErr.put("Message ", "You are not logged in!");
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.FORBIDDEN);
        }
    }
}
