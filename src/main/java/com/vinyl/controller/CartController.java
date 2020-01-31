package com.vinyl.controller;

import com.vinyl.DTO.CartItemDTO;
import com.vinyl.DTO.CartItemQuantityDTO;
import com.vinyl.DTO.MessageDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.Cart;
import com.vinyl.model.CartItem;
import com.vinyl.model.Item;
import com.vinyl.service.CartItemService;
import com.vinyl.service.CartService;
import com.vinyl.service.ItemService;
import com.vinyl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/VinylStore/api")
public class CartController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ItemService itemService;

    @GetMapping(value = "/cart/customer/detail", produces = "application/json")
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String auth){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();

        cartItem.forEach(cartItem1 -> cartItemDTOS.add(CartItemDTO.build(cartItem1)));

        if(cartItem.size() == 0) {
            return MessageResponse("No items in cart!", HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(cartItemDTOS, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/cart/customer/{item_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> addVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long item_id, @RequestBody CartItemQuantityDTO cartItemQuantityDTO){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        MessageDTO messageDTO = new MessageDTO();

        Optional<Item> optionalItem = itemService.findById(item_id);
        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        CartItem cartItem = new CartItem();

        if (cartItemQuantityDTO.getQuantity() <= 0) {
            return MessageResponse("Quantity can't be negative or zero!", HttpStatus.FORBIDDEN);
        }
        else {
            if (cartItemQuantityDTO.getQuantity() > optionalItem.get().getQuantity()) {
                return MessageResponse("Quantity can't be negative or zero!", HttpStatus.FORBIDDEN);
            } else {
                if (cartItemService.findByItemIdAndCartId(item_id, cart.getId()).isPresent() && cartItemService.findByItemIdAndCartId(item_id, cart.getId()).get().getCart() != null) {
                    cartItem = cartItemService.findByItemIdAndCartId(item_id, cart.getId()).get();
                    messageDTO.setMessage("Item updated from cart!");
                } else {
                    cartItem.setItem(optionalItem.get());
                    cartItem.setCart(cart);
                    messageDTO.setMessage("Item added to cart!");
                    }
                }
            }

        cartItem.setQuantity(cartItemQuantityDTO.getQuantity());
        cartItemService.save(cartItem);

        return MessageResponse(messageDTO.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/cart/customer/{item_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> removeVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long item_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        Cart cart = cartService.findByUserId(userService.findByEmailAddress(email).getId());
        List<CartItem> cartItem = cartItemService.findByCartId(cart.getId());

        if(!cartItem.isEmpty()) {
            cartItem.forEach(cItem -> {
                if (cItem.getItem().getId().equals(item_id))
                    cartItemService.delete(cItem);
            });
            return MessageResponse("Item deleted from cart!", HttpStatus.OK);
        }
        else {
            return MessageResponse("No items in cart!", HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<MessageDTO> MessageResponse(String message, HttpStatus httpStatus){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        return new ResponseEntity<>(messageDTO, httpStatus);
    }
}