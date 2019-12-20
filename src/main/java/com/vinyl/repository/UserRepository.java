package com.vinyl.repository;

import com.vinyl.model.User;
import com.vinyl.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmailAddress(String emailAddress);
        List<User> findByUserRole(UserRole userRole);
}
