package com.project.tarefas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.model.User;
import com.project.tarefas.model.DTO.UserRegistrationDTO;
import com.project.tarefas.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
	private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        User newUser = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(newUser);
    }
}
