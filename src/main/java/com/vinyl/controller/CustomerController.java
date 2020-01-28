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

    @GetMapping(value = "/customer/cart/detail", produces = "application/json")
    public ResponseEntity<Object> getCart(@RequestHeader("Authorization") String auth){
        MessageDTO messageDTO = new MessageDTO();
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();

        cartItem.forEach(cartItem1 -> {
            cartItemDTOS.add(CartItemDTO.build(cartItem1));
        });

        if(cartItem.size() == 0){
            messageDTO.setMessage("No items in cart!");
            return new ResponseEntity<>(messageDTO, HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>(cartItemDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/vinyls/cart/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> addVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id, @RequestBody CartItemQuantityDTO cartItemQuantityDTO){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();
        if (itemService.findById(vinyl_id).isPresent()){
            Optional<Item> optionalItem = itemService.findById(vinyl_id);
            Item item = optionalItem.get();
            Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
            CartItem cartItem = new CartItem();

            if (cartItemQuantityDTO.getQuantity() <= 0){
                messageDTO.setMessage("Quantity can't be negative or zero!");
                return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
            }
            else if (cartItemQuantityDTO.getQuantity() > item.getQuantity()) {
                messageDTO.setMessage("Quantity can't be negative or zero!");
                return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
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
                return ResponseEntity.ok(messageDTO);
            }
        }
        else{
            messageDTO.setMessage("Not a valid item ID!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/users/cart/{item_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> removeVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long item_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

        if(!cartItem.isEmpty()) {
                cartItem.forEach(cItem -> {
                    if (cItem.getItem().getId().equals(item_id)) cartItemService.delete(cItem);
                });
                messageDTO.setMessage("Item deleted from cart!");
                return ResponseEntity.ok(messageDTO);
        }
        else{
            messageDTO.setMessage("No items in cart!");
            return new ResponseEntity<>(messageDTO, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/orders", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> placeOrder(@RequestHeader("Authorization") String auth){
        MessageDTO messageDTO = new MessageDTO();
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
                messageDTO.setMessage("No more items availabile!");
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
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
                messageDTO.setMessage("Order placed!");
                return ResponseEntity.ok(messageDTO);
            }
    }

    @GetMapping(value = "/users/orders", produces = "application/json")
    public ResponseEntity<Object> getUserOrder(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        List<Order> orders = orderService.findByUserId(userService.findByEmailAddress(email).getId());
        List<OrderDTO> orderDTOS = new ArrayList<>();

        if(orders.isEmpty())
            {
                messageDTO.setMessage("No Orders placed!");
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }

        orders.forEach(order -> {
            orderDTOS.add(OrderDTO.build(order));
        });

        return new ResponseEntity<>(orderDTOS, HttpStatus.OK);
    }
}
