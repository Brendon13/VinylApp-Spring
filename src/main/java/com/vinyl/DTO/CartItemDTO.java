package com.vinyl.DTO;

import com.vinyl.model.CartItem;

public class CartItemDTO {
    private Long id;

    private String name;

    private Double price;

    private String description;

    private Long quantity;

    public CartItemDTO(Long id, String name, Double price, String description, Long quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public static CartItemDTO build(CartItem cartItem){
        return new CartItemDTO(cartItem.getItem().getId(), cartItem.getItem().getName(), cartItem.getItem().getPrice(), cartItem.getItem().getDescription(), cartItem.getQuantity());
    }

}
