package com.vinyl.service;

import com.vinyl.model.User;
import com.vinyl.model.UserRole;

import java.util.List;

public interface UserService {
    void save(User user);
    void saveManager(User user);
    User findByEmailAddress(String emailAddress);
    List<User> findByUserRole(UserRole userRole);
    void delete(User user);
    User findById(Long id);
}
