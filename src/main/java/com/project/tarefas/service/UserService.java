package com.project.tarefas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tarefas.DTO.LoginResponseDTO;
import com.project.tarefas.DTO.PasswordChangeDTO;
import com.project.tarefas.DTO.UserLoginDTO;
import com.project.tarefas.DTO.UserRegistrationDTO;
import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.exception.InvalidPasswordException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.model.User;
import com.project.tarefas.mapper.UserMapper;
import com.project.tarefas.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public LoginResponseDTO loginUser(UserLoginDTO userLogin) throws UserNotFoundException, InvalidPasswordException {
    	User user = userRepository.findByUsername(userLogin.getUsername())
    			.orElseThrow(() -> new UserNotFoundException("User not exist"));
    	
    	if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
    		throw new InvalidPasswordException("Current password is incorrect");
    	}
    	
    	String token = jwtService.generateToken(user);
    	
    	return new LoginResponseDTO(
                token,
                user.getId(),
                user.getUsername()
            );
    }

    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
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
        
        userRepository.save(user);
        
        return userMapper.toResponseDTO(user);
    }
    
    public void changePassword(Long userId, PasswordChangeDTO changeDTO) throws UserNotFoundException, InvalidPasswordException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(changeDTO.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (changeDTO.getNewPassword().equals(changeDTO.getCurrentPassword())) {
            throw new InvalidPasswordException("New password must be different from current");
        }

        if (!changeDTO.getNewPassword().equals(changeDTO.getConfirmation())) {
            throw new InvalidPasswordException("New password and confirmation don't match");
        }

        user.setPassword(passwordEncoder.encode(changeDTO.getNewPassword()));
        userRepository.save(user);
    }
    
    public UserResponseDTO findUserById(Long id) throws UserNotFoundException {
    	User user = userRepository.findById(id)
    			.orElseThrow(() -> new UserNotFoundException("User not exist"));
    	
    	return userMapper.toResponseDTO(user);
    }
    
    public List<UserResponseDTO> findUserAll() {
    	List<User> users = userRepository.findAll();
    	
    	return userMapper.toResponseDTOList(users);
    }
    
}