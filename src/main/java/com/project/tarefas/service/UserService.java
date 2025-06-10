package com.project.tarefas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tarefas.model.User;
import com.project.tarefas.model.DTO.UserRegistrationDTO;
import com.project.tarefas.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setName(registrationDTO.getName());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        
        return userRepository.save(user);
    }
}