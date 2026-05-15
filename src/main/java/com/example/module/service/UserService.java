package com.example.module.service;

import com.example.module.entity.Users;
import com.example.module.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveNewUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveEntry(Users user){
        userRepository.save(user);
    }

    public List<Users> getAllEntries(){
        return userRepository.findAll();
    }

    public Users findByUsername(String username){
        // Now returns Optional, use orElse(null) or orElseThrow()
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<Users> findByUsernameOptional(String username){
        return userRepository.findByUsername(username);
    }
}