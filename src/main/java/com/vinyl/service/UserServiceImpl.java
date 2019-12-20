package com.vinyl.service;

import com.vinyl.model.User;
import com.vinyl.model.UserRole;
import com.vinyl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserRole(new UserRole((long)1,"customer"));
        userRepository.save(user);
    }

    @Override
    public void saveManager(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserRole(new UserRole((long)2,"manager"));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(User user){
        userRepository.deleteById(user.getId());
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress);
    }

    @Override
    public User findById(Long id){
        return userRepository.getOne(id);
    }

    @Override
    public List<User> findByUserRole(UserRole userRole){
        return userRepository.findByUserRole(userRole);
    }

}
