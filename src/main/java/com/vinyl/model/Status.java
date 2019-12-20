package com.vinyl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_status")
public class Status {
    @Id
    @GeneratedValue
    private Long id;

    private String status;

    public Status(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public Status(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


