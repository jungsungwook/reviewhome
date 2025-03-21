package com.memeki.reviewhome.global.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean isExistEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
