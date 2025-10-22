package com.project.tarefas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.LoginResponseDTO;
import com.project.tarefas.DTO.UserLoginDTO;
import com.project.tarefas.DTO.UserRegistrationDTO;
import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.exception.InvalidPasswordException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
    	UserResponseDTO response = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody UserLoginDTO loginDTO) throws UserNotFoundException, InvalidPasswordException {
    	LoginResponseDTO response = userService.loginUser(loginDTO);
    	return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build(); 
    }
    
}
