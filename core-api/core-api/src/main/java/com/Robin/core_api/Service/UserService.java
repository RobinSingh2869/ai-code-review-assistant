package com.Robin.core_api.Service;

import com.Robin.core_api.Model.Entity.User;
import com.Robin.core_api.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder encoder;

    public User register(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
}
