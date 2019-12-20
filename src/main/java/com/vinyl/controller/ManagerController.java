package com.vinyl.controller;

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

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
@Api(value="ManagerController", description="Manager operations for the Vinyl Store")
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

    @ApiOperation(value = "Add vinyl to store", response = Iterable.class)
    @PostMapping(value = "/vinylsAdd")
    public ResponseEntity<?> addVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        Item item = new Item();
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0)
                    return new ResponseEntity<>("Quantity can't be negative!", HttpStatus.FORBIDDEN);
                else if(vinyl.getPrice() <= 0)
                    return new ResponseEntity<>("Price can't be negative!", HttpStatus.FORBIDDEN);
                else if(itemService.findByName(vinyl.getName()) == null){
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    itemService.save(item);

                    return ResponseEntity.ok("Item inserted!");
                }
                else return new ResponseEntity<>("Item already exists!", HttpStatus.FORBIDDEN);
            }
            catch (NullPointerException e){
                return new ResponseEntity<>("Quantity can't be null!", HttpStatus.FORBIDDEN);
            }
        }
        else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Delete vinyl from store", response = Iterable.class)
    @DeleteMapping(value = "/vinyls/{vinyl_id}")
    public @ResponseBody ResponseEntity<?> deleteVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id) {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            if(itemService.findById(vinyl_id) != null){
                itemService.delete(itemService.findById(vinyl_id));
                return new ResponseEntity<>("Item Deleted!", HttpStatus.NO_CONTENT);
            }
            else{
                return new ResponseEntity<>("Item does not exist!", HttpStatus.NOT_FOUND);
            }
        }
        else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Update vinyl", response = Iterable.class)
    @PutMapping(value = "/vinyls/{vinyl_id}")
    public @ResponseBody ResponseEntity<?> updateVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl, @PathVariable Long vinyl_id){
        Item item = new Item();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0)
                    return new ResponseEntity<>("Quantity can't be negative!", HttpStatus.FORBIDDEN);
                else if(vinyl.getPrice() <= 0)
                    return new ResponseEntity<>("Price can't be negative!", HttpStatus.FORBIDDEN);
                else if(itemService.findById(vinyl_id) != null){
                    item.setQuantity(vinyl.getQuantity());
                    item.setDescription(vinyl.getDescription());
                    item.setName(vinyl.getName());
                    item.setPrice(vinyl.getPrice());
                    item.setId(vinyl_id);
                    itemService.save(item);

                    return ResponseEntity.ok("Item updated!");
                }
                else return new ResponseEntity<>("Item doesn't exists!", HttpStatus.FORBIDDEN);
            }
            catch (NullPointerException e){
                return new ResponseEntity<>("Quantity can't be null!", HttpStatus.FORBIDDEN);
            }
        }
        else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Update order", response = Iterable.class)
    @PutMapping(value = "/orders/{order_id}")
    public @ResponseBody ResponseEntity<?> updateOrder(@RequestHeader("Authorization") String auth, @RequestParam Long status, @PathVariable Long order_id){
        Order order;
        Date date = new Date();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            if(orderService.findById(order_id) != null){
                if(status == 1 || status == 2){
                    order = orderService.findById(order_id);
                    order.setStatus(statusService.findById(status));
                    order.setUpdatedAt(date);
                    orderService.save(order);

                    return ResponseEntity.ok("Order status changed!");
                }
                else return new ResponseEntity<>("Status is not a valid Id!", HttpStatus.FORBIDDEN);
            }
            else return new ResponseEntity<>("Order doesn't exist!", HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Get all customers", response = Iterable.class)
    @GetMapping(value = "/customers")
    public ResponseEntity<?> getCustomers(@RequestHeader("Authorization") String auth) throws JSONException{
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            UserRole userRole = new UserRole(1L,"customer");
            List<User> users = userService.findByUserRole(userRole);

            JSONObject json = new JSONObject();
            JSONArray json3 = new JSONArray();

            for(int i = 0; i< (long) users.size(); i++){
                JSONObject json2 = new JSONObject();
                json2.put("Email", users.get(i).getEmailAddress());
                json2.put("First Name", users.get(i).getFirstName());
                json2.put("Last Name", users.get(i).getLastName());
                json3.put(json2);
            }

            json.put("Customers", json3);

            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        }
        else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Get order from an user", response = Iterable.class)
    @GetMapping(value = "/users/{user_id}/orders")
    public ResponseEntity<?> getUserOrder(@RequestHeader("Authorization") String auth, @PathVariable Long user_id) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
            if (userService.findByEmailAddress(email).getUserRole().getId() == 2) {
                    if (userService.findById(user_id) == null) {
                        return new ResponseEntity<>("User id doesn't exist!", HttpStatus.BAD_REQUEST);
                    } else {

                        List<Order> orders = orderService.findByUserId(user_id);
                        JSONObject json = new JSONObject();
                        JSONArray json3 = new JSONArray();

                        for (int i = 0; i < (long) orders.size(); i++) {
                            JSONObject json2 = new JSONObject();
                            json2.put("Id", orders.get(i).getId());
                            json2.put("Cost", orders.get(i).getTotal_price());
                            json2.put("Order Date", orders.get(i).getCreatedAt());
                            json2.put("Status", orders.get(i).getStatus().getStatus());
                            json3.put(json2);
                        }

                        json.put("Orders", json3);

                        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
                    }
            } else return new ResponseEntity<>("You are not a manager!", HttpStatus.FORBIDDEN);

    }
}
