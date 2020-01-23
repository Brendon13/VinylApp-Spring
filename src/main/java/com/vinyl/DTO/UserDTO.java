package com.vinyl.DTO;

import com.vinyl.model.User;

public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String emailAddress;

    public UserDTO(Long id, String firstName, String lastName, String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public static UserDTO build(User user){
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmailAddress());
    }
}
