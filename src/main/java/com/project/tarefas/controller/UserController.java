package com.project.tarefas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.PasswordChangeDTO;
import com.project.tarefas.DTO.UserLoginDTO;
import com.project.tarefas.DTO.UserRegistrationDTO;
import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.exception.InvalidPasswordException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
	private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
    	UserResponseDTO newUser = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(newUser);
    }
    
    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<Void> changePassword(
        @PathVariable Long userId,
        @RequestBody PasswordChangeDTO passwordChangeDTO) throws UserNotFoundException, InvalidPasswordException {
        
        userService.changePassword(userId, passwordChangeDTO);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody UserLoginDTO loginDTO) throws UserNotFoundException, InvalidPasswordException {
    	UserResponseDTO user = userService.loginUser(loginDTO);
    	return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long userId) throws UserNotFoundException {
    	UserResponseDTO users = userService.findUserById(userId);
    	
    	return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findUserAll() {
    	List<UserResponseDTO> users = userService.findUserAll();
    	
    	return ResponseEntity.ok(users);
    }
}
