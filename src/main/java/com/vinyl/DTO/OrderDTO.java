package com.vinyl.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinyl.model.Order;

import java.util.Date;

public class OrderDTO {
    private Long id;

    private Double total_price;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    private String status;

    public OrderDTO(Long id, Double total_price, Date createdAt, Date updatedAt, String status) {
        this.id = id;
        this.total_price = total_price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static OrderDTO build(Order order){
        return new OrderDTO(order.getId(), order.getTotal_price(), order.getCreatedAt(), order.getUpdatedAt(), order.getStatus().getStatus());
    }
}
