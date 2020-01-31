package com.vinyl.controller;

import com.vinyl.DTO.MessageDTO;
import com.vinyl.DTO.OrderDTO;
import com.vinyl.DTO.StatusDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.Cart;
import com.vinyl.model.CartItem;
import com.vinyl.model.Order;
import com.vinyl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
public class OrderController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatusService statusService;

    @PostMapping(value = "/orders", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> placeOrder(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        List<Boolean> quantityResponse = new ArrayList<>();
        Order order;

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

        double totalPrice = 0;

        for(int i = 0; i< (long) cartItem.size(); i++){
            totalPrice += cartItem.get(i).getQuantity()*cartItem.get(i).getItem().getPrice();
        }

        cartItem.forEach(cItem -> {
            if(cItem.getQuantity()>cItem.getItem().getQuantity())
                quantityResponse.add(false);
        });

        if(quantityResponse.contains(false)) {
            return MessageResponse("No more items available!", HttpStatus.BAD_REQUEST);
        }
        else {
            cartItem.forEach(cItem -> cItem.getItem().setQuantity(cItem.getItem().getQuantity()-cItem.getQuantity()));
            order = new Order(totalPrice, new Date(), new Date(), userService.findById(userService.findByEmailAddress(email).getId()), statusService.findById(1L));
            orderService.save(order);

            cartItem.forEach(cItem -> {
                cartItemService.delete(cItem);
            });
            return MessageResponse("Order placed!", HttpStatus.OK);
        }
    }

    @PutMapping(value = "/orders/{order_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> updateOrder(@RequestHeader("Authorization") String auth, @RequestBody StatusDTO status, @PathVariable Long order_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        Order order;

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            if(orderService.findById(order_id).isPresent()){
                if(status.getId() == 1 || status.getId() == 2){
                    order = orderService.findById(order_id).get();
                    order.setStatus(statusService.findById(status.getId()));
                    order.setUpdatedAt(new Date());
                    orderService.save(order);

                    return MessageResponse("Order status changed!", HttpStatus.OK);
                }
                else {
                    return MessageResponse("Status is not a valid Id!", HttpStatus.FORBIDDEN);
                }
            }
            else {
                return MessageResponse("Order doesn't exist!", HttpStatus.NOT_FOUND);
            }
        }
        else {
            return MessageResponse("You are not a manager!", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/orders/{user_id}", produces = "application/json")
    public ResponseEntity<?> getUserOrder(@RequestHeader("Authorization") String auth, @PathVariable Long user_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if (userService.findByEmailAddress(email).getUserRole().getId() == 2) {
            if (userService.findById(user_id) == null) {
                return MessageResponse("User id doesn't exist!", HttpStatus.BAD_REQUEST);
            }
            else {
                List<Order> orders = orderService.findByUserId(user_id);
                List<OrderDTO> orderDTOS = new ArrayList<>();

                orders.forEach(order -> {
                    orderDTOS.add(OrderDTO.build(order));
                });

                if(orderDTOS.isEmpty()) {
                    return MessageResponse("No orders placed!", HttpStatus.NOT_FOUND);
                }
                else {
                    return new ResponseEntity<>(orderDTOS, HttpStatus.OK);
                }
            }
        }
        else {
            return MessageResponse("You are not a manager!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/orders/user", produces = "application/json")
    public ResponseEntity<?> getOrder(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        List<Order> orders = orderService.findByUserId(userService.findByEmailAddress(email).getId());
        List<OrderDTO> orderDTOS = new ArrayList<>();

        if(orders.isEmpty()) {
            return MessageResponse("No Orders placed!", HttpStatus.BAD_REQUEST);
        }

        orders.forEach(order -> orderDTOS.add(OrderDTO.build(order)));

        return new ResponseEntity<>(orderDTOS, HttpStatus.OK);
    }

    public ResponseEntity<MessageDTO> MessageResponse(String message, HttpStatus httpStatus){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        return new ResponseEntity<>(messageDTO, httpStatus);
    }
}