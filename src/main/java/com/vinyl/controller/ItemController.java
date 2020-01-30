package com.vinyl.controller;

import com.vinyl.DTO.ItemDTO;
import com.vinyl.DTO.MessageDTO;
import com.vinyl.config.JwtTokenUtil;
import com.vinyl.model.Item;
import com.vinyl.service.ItemService;
import com.vinyl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/VinylStore/api")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/vinyls", produces = "application/json")
    public List<ItemDTO> getVinyl(@RequestHeader("Authorization") String auth){
        List<Item> item = itemService.findAll();
        List<ItemDTO> itemDTOS = new ArrayList<>();
        item.forEach(item1 -> itemDTOS.add(ItemDTO.build(item1)));
        return itemDTOS;
    }

    @GetMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public ResponseEntity<?> getVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id){
        if(itemService.findById(vinyl_id).isPresent()){
            ItemDTO itemDTO = ItemDTO.build(itemService.findById(vinyl_id).get());
            return new ResponseEntity<>(itemDTO, HttpStatus.OK);
        }
        else return MessageResponse("Not a valid Id!", HttpStatus.FORBIDDEN);
    }

    @PutMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> updateVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl, @PathVariable Long vinyl_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0)
                    return MessageResponse("Quantity can't be negative!", HttpStatus.FORBIDDEN);
                else if(vinyl.getPrice() <= 0)
                    return MessageResponse("Price can't be negative!", HttpStatus.FORBIDDEN);
                    else {
                        if (itemService.findById(vinyl_id).isPresent()) {
                            itemService.save(new Item(vinyl_id, vinyl.getName(), vinyl.getPrice(), vinyl.getDescription(), vinyl.getQuantity()));
                            return MessageResponse("Item updated!", HttpStatus.OK);
                        }
                        else return MessageResponse("Item doesn't exists!", HttpStatus.FORBIDDEN);
                    }
            }
            catch (NullPointerException e){
                return MessageResponse("Quantity can't be null!", HttpStatus.FORBIDDEN);
            }
        }
        else return MessageResponse("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    @PostMapping(value = "/vinyls/add", produces = "application/json")
    public ResponseEntity<MessageDTO> addVinyl(@RequestHeader("Authorization") String auth, @RequestBody Item vinyl){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));
        Item item;

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try {
                if (vinyl.getQuantity() <= 0){
                    return MessageResponse("Quantity can't be negative!", HttpStatus.FORBIDDEN);
                }
                else if(vinyl.getPrice() <= 0){
                    return MessageResponse("Price can't be negative!", HttpStatus.FORBIDDEN);
                }
                    else if(itemService.findByName(vinyl.getName()) == null){
                            item = new Item(vinyl.getName(), vinyl.getPrice(), vinyl.getDescription(), vinyl.getQuantity());
                            itemService.save(item);
                            return MessageResponse("Item inserted!", HttpStatus.OK);
                        }
                        else return MessageResponse("Item already exists!", HttpStatus.FORBIDDEN);
            }
            catch (NullPointerException e){
                return MessageResponse("Quantity can't be null!", HttpStatus.FORBIDDEN);
            }
        }
        else return MessageResponse("You are not a manager!", HttpStatus.FORBIDDEN);

    }

    @DeleteMapping(value = "/vinyls/{vinyl_id}", produces = "application/json")
    public @ResponseBody ResponseEntity<MessageDTO> deleteVinyl(@RequestHeader("Authorization") String auth, @PathVariable Long vinyl_id){
        String email = jwtTokenUtil.getUsernameFromToken(auth.substring(7));

        if(userService.findByEmailAddress(email).getUserRole().getId() == 2){
            try{
                if(itemService.findById(vinyl_id).isPresent()){
                    Item item = itemService.findById(vinyl_id).get();
                    itemService.delete(item);

                    return MessageResponse("Item Deleted!", HttpStatus.NO_CONTENT);
                }
                else return MessageResponse("Item doesn't exists!", HttpStatus.NOT_FOUND);
            }
            catch (NullPointerException e) {
                return MessageResponse("Item doesn't exists!", HttpStatus.NOT_FOUND);
            }
        }
        else return MessageResponse("You are not a manager!", HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<MessageDTO> MessageResponse(String message, HttpStatus httpStatus){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message);
        return new ResponseEntity<>(messageDTO, httpStatus);
    }
}