package com.vinyl.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotNull(message = "Price can't be blank")
    private Double price;

    @NotBlank(message = "Description can't be blank")
    private String description;

    @NotNull(message = "Quantity can't be blank")
    private Long quantity;

    @OneToOne(mappedBy="item", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private CartItem cartItem;

    public Item(@NotBlank(message = "Name can't be blank") String name, @NotNull(message = "Price can't be blank") Double price, @NotBlank(message = "Description can't be blank") String description, @NotNull(message = "Quantity can't be blank") Long quantity) {
        setName(name);
        setPrice(price);
        setDescription(description);
        setQuantity(quantity);
    }

    public Item(Long id, @NotBlank(message = "Name can't be blank") String name, @NotNull(message = "Price can't be blank") Double price, @NotBlank(message = "Description can't be blank") String description, @NotNull(message = "Quantity can't be blank") Long quantity) {
        setId(id);
        setName(name);
        setPrice(price);
        setDescription(description);
        setQuantity(quantity);
    }

    public Item(){}

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

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}

