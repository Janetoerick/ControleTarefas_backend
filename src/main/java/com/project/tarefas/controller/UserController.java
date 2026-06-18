package com.project.tarefas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tarefas.DTO.PasswordChangeDTO;
import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
	private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Muda a senha do usuario.
     */
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
        @PathVariable Long userId,
        @RequestBody PasswordChangeDTO passwordChangeDTO) {
        
        userService.changePassword(userId, passwordChangeDTO);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Achar o usuario atraves do Id
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable("userId") Long userId) {
    	UserResponseDTO user = userService.findUserById(userId);
    	
    	return ResponseEntity.ok(user);
    }
    
    /**
     * Pega todos os usuarios do sistema.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> findUserAll() {
    	List<UserResponseDTO> users = userService.findUserAll();
    	
    	return ResponseEntity.ok(users);
    }
}
