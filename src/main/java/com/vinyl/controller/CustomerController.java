package com.vinyl.controller;

import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/VinylStore/api")
@Api(value="CustomerController", description="Customer operations for the Vinyl Store")
public class CustomerController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @ApiOperation(value = "Get cart details", response = Iterable.class)
    @GetMapping(value = "/customer/cart/detail", produces = "application/json")
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String auth) throws JSONException {
        JSONObject jsonError = new JSONObject();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 1) {
            Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
            List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());
            double totalPrice = 0;

            for (int i = 0; i < (long) cartItem.size(); i++) {
                totalPrice += cartItem.get(i).getQuantity() * cartItem.get(i).getItem().getPrice();
            }

            JSONObject json = new JSONObject();
            json.put("NumberOfItems", cartItem.size());
            json.put("TotalCost", totalPrice);

            JSONArray json3 = new JSONArray();

            for (int i = 0; i < (long) cartItem.size(); i++) {
                JSONObject json2 = new JSONObject();
                json2.put("Id", cartItem.get(i).getItem().getId());
                json2.put("Name", cartItem.get(i).getItem().getName());
                json2.put("Description", cartItem.get(i).getItem().getDescription());
                json2.put("Price", cartItem.get(i).getItem().getPrice());
                json2.put("Quantity", cartItem.get(i).getQuantity());
                json3.put(json2);
            }

            json.put("ItemsInCart", json3);


            if(cartItem.size() == 0){
                jsonError.put("Message", "No items in cart!");
                return new ResponseEntity<>(jsonError.toString(), HttpStatus.NOT_FOUND);
            }
            else return new ResponseEntity<>(json3.toString(), HttpStatus.OK); //json.toString() to display as in req for backend
        }
        else
            {
            jsonError.put("Message", "You are manager!");
            return new ResponseEntity<>(jsonError.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Add vinyl to cart", response = Iterable.class)
    @PostMapping(value = "/vinyls/cart/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> addVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id, @RequestBody CartItemDTO cartItemDTO) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
            try {
                if (itemService.findById(vinyl_id).isPresent()){
                    Optional<Item> optionalItem = itemService.findById(vinyl_id);
                    Item item = optionalItem.get();
                    Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
                    CartItem cartItem = new CartItem();

                    if (cartItemDTO.getQuantity() <= 0){
                        json.put("Message ", "Quantity can't be negative or zero!");
                        return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                    }
                    else if (cartItemDTO.getQuantity() > item.getQuantity()) {
                        json.put("Message ", "Quantity too big!");
                        return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                    } else {
                        if(cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).isPresent() && cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).get().getCart() != null){
                            cartItem = cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).get();
                            json.put("Message ", "Item updated from cart!");
                        }
                        else {
                            cartItem.setItem(item);
                            cartItem.setCart(cart);
                            json.put("Message ", "Item added to cart!");
                        }
                        cartItem.setQuantity(cartItemDTO.getQuantity());
                        cartItemService.save(cartItem);
                        return ResponseEntity.ok(json.toString());
                    }
                }
                else{
                    json.put("Message ", "Not a valid item ID!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
            }
            catch (JSONException e){
                json.put("Message ", "Quantity can't be null!");
                return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
            }
    }

    @ApiOperation(value = "Remove vinyl from cart", response = Iterable.class)
    @DeleteMapping(value = "/users/cart/{item_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<?> removeVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long item_id) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        final Boolean[] noItems = {null};

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

        if(!cartItem.isEmpty()) {
            if (userService.findByEmailAddress(email).getId().equals(userService.findByEmailAddress(email).getId())) {
                cartItem.forEach(cItem -> {
                    if (cItem.getItem().getId().equals(item_id)){
                        cartItemService.delete(cItem);
                        noItems[0] = false;
                    }
                    else noItems[0] = true;
                });
                if (noItems[0]){
                    json.put("Message ", "No items with that ID in cart!");
                    return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
                }
                else{
                    json.put("Message ", "Item deleted from cart!");
                    return ResponseEntity.ok(json.toString());
                }
            } else{
                json.put("Message ", "You are not logged in");
                return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
            }
        }
        else{
            json.put("Message ", "No items in cart!");
            return new ResponseEntity<>(json.toString(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Place order", response = Iterable.class)
    @PutMapping(value = "/orders", produces = "application/json")
    public @ResponseBody ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String auth) throws JSONException {
        JSONObject json = new JSONObject();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
            List <Boolean> quantityResponse = new ArrayList<>();
            Order order = new Order();

            Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
            List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

            double totalPrice = 0;

            for(int i = 0; i< (long) cartItem.size(); i++){
                totalPrice+= cartItem.get(i).getQuantity()*cartItem.get(i).getItem().getPrice();
            }


            cartItem.forEach(cItem -> {
                if(cItem.getQuantity()>cItem.getItem().getQuantity())
                    quantityResponse.add(false);
            });

            if(quantityResponse.contains(false)) {
                json.put("Message ", "No more items availabile!");
                return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
            }
            else{
                cartItem.forEach(cItem -> cItem.getItem().setQuantity(cItem.getItem().getQuantity()-cItem.getQuantity()));
                order.setUser(userService.findById(userService.findByEmailAddress(email).getId()));
                order.setCreatedAt(new Date());
                order.setUpdatedAt(new Date());
                order.setStatus(statusService.findById(1L));
                order.setTotal_price(totalPrice);
                orderService.save(order);

                cartItem.forEach(cItem -> {
                    cartItemService.delete(cItem);
                });
                json.put("Message ", "Order placed!");
                return ResponseEntity.ok(json.toString());
            }
    }

    @ApiOperation(value = "Get order", response = Iterable.class)
    @GetMapping(value = "/users/orders", produces = "application/json")
    public ResponseEntity<?> getUserOrder(@RequestHeader("Authorization") String auth) throws JSONException {
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        JSONObject json = new JSONObject();
        List<Order> orders = orderService.findByUserId(userService.findByEmailAddress(email).getId());

        if(orders.isEmpty())
            {
                json.put("Message ", "No Orders placed!");
                return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
            }

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
}
