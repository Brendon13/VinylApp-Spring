package com.vinyl.controller;

import com.vinyl.DTO.CartItemDTO;
import com.vinyl.DTO.CartItemQuantityDTO;
import com.vinyl.DTO.MessageDTO;
import com.vinyl.DTO.OrderDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.*;
import com.vinyl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/VinylStore/api")
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

    @PostMapping(value = "/users", produces = "application/json")
    public void addUser(@Valid @RequestBody User user){
        if (userService.findByEmailAddress(user.getEmailAddress()) == null) {
            userService.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartService.save(cart);

            MessageResponse("User Created!", HttpStatus.OK);
        } else {
            MessageResponse("Email already in use!", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/customer/cart/detail", produces = "application/json")
    public ResponseEntity<Object> getCart(@RequestHeader("Authorization") String auth){
        MessageDTO messageDTO = new MessageDTO();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();

        cartItem.forEach(cartItem1 -> cartItemDTOS.add(CartItemDTO.build(cartItem1)));

        if(cartItem.size() == 0){
            messageDTO.setMessage("No items in cart!");
            return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>(cartItemDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/vinyls/cart/{vinyl_id}", produces = "application/json")
    public @ResponseBody
    void addVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id, @RequestBody CartItemQuantityDTO cartItemQuantityDTO){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        if (itemService.findById(vinyl_id).isPresent()){
            Optional<Item> optionalItem = itemService.findById(vinyl_id);
            Item item = optionalItem.get();
            Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
            CartItem cartItem = new CartItem();

            if (cartItemQuantityDTO.getQuantity() <= 0){
                MessageResponse("Quantity can't be negative or zero!", HttpStatus.FORBIDDEN);
            }
            else if (cartItemQuantityDTO.getQuantity() > item.getQuantity()) {
                MessageResponse("Quantity can't be negative or zero!", HttpStatus.FORBIDDEN);
            } else {
                if(cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).isPresent() && cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).get().getCart() != null){
                    cartItem = cartItemService.findByItemIdAndCartId(vinyl_id, cart.getId()).get();
                    messageDTO.setMessage("Item updated from cart!");
                }
                else {
                    cartItem.setItem(item);
                    cartItem.setCart(cart);
                    messageDTO.setMessage("Item added to cart!");
                }
                cartItem.setQuantity(cartItemQuantityDTO.getQuantity());
                cartItemService.save(cartItem);

                MessageResponse(messageDTO.getMessage(), HttpStatus.OK);
            }
        }
        else{
            MessageResponse("Not a valid item ID!", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/users/cart/{item_id}", produces = "application/json")
    public @ResponseBody
    void removeVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long item_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

        if(!cartItem.isEmpty()) {
                cartItem.forEach(cItem -> {
                    if (cItem.getItem().getId().equals(item_id))
                        cartItemService.delete(cItem);
                });
                MessageResponse("Item deleted from cart!", HttpStatus.OK);
        }
        else{
            MessageResponse("No items in cart!", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/orders", produces = "application/json")
    public @ResponseBody
    void placeOrder(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        List <Boolean> quantityResponse = new ArrayList<>();
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

        if(quantityResponse.contains(false))
            MessageResponse("No more items availabile!", HttpStatus.BAD_REQUEST);
        else{
            cartItem.forEach(cItem -> cItem.getItem().setQuantity(cItem.getItem().getQuantity()-cItem.getQuantity()));
            order = new Order(totalPrice, new Date(), new Date(), userService.findById(userService.findByEmailAddress(email).getId()), statusService.findById(1L));
            orderService.save(order);

            cartItem.forEach(cItem -> {
                cartItemService.delete(cItem);
            });
            MessageResponse("Order placed!", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/users/orders", produces = "application/json")
    public ResponseEntity<Object> getUserOrder(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        List<Order> orders = orderService.findByUserId(userService.findByEmailAddress(email).getId());
        List<OrderDTO> orderDTOS = new ArrayList<>();

        if(orders.isEmpty()) {
            MessageResponse("No Orders placed!", HttpStatus.BAD_REQUEST);
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
