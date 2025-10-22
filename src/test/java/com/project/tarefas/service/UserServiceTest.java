package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.project.tarefas.DTO.LoginResponseDTO;
import com.project.tarefas.DTO.PasswordChangeDTO;
import com.project.tarefas.DTO.UserLoginDTO;
import com.project.tarefas.DTO.UserResponseDTO;
import com.project.tarefas.mapper.UserMapper;
import com.project.tarefas.model.User;
import com.project.tarefas.repository.UserRepository;
import com.project.tarefas.exception.InvalidPasswordException;
import com.project.tarefas.exception.UserNotFoundException;
import com.project.tarefas.exception.InvalidConfirmationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserLoginDTO userLoginDTO;
    private UserResponseDTO userResponseDTO;
    private final String ENCODED_PASSWORD = "hashedPassword123";
    private final String MOCK_TOKEN = "mocked.jwt.token";
    
    private PasswordChangeDTO passwordChangeDTO;
    private final String NEW_PASSWORD = "NewValidPassword123";

    @BeforeEach
    void setUp() {
    	user = new User(1L, "TestUser", "test@mail.com", "user1", ENCODED_PASSWORD);
        userLoginDTO = new UserLoginDTO("TestUser", "rawPassword");
        userResponseDTO = new UserResponseDTO(1L, "TestUser", "test@mail.com", "user1");
        
        passwordChangeDTO = new PasswordChangeDTO(
                "rawPassword",
                NEW_PASSWORD,
                NEW_PASSWORD
            );
    }
    
 // =============================================================================================================
 //  TESTANDO AS FUNÇÕES DE LOGIN 
 // =============================================================================================================

    @Test
    @DisplayName("Deve realizar o login com sucesso e retornar o Token")
    void loginUser_ShouldReturnLoginResponseDTO_WhenCredentialsAreValid() throws Exception {
        // ARRANGE
        // 1. Configura o Repository para encontrar o usuário
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        
        // 2. Configura o PasswordEncoder para validar a senha
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // 3. Configura o JwtService para gerar um token
        when(jwtService.generateToken(any(User.class))).thenReturn(MOCK_TOKEN);

        LoginResponseDTO result = userService.loginUser(userLoginDTO);

        // ASSERT
        // 1. Verifica se o resultado não é nulo
        assertNotNull(result);
        // 2. Verifica se o token está correto
        assertEquals(MOCK_TOKEN, result.getToken());
        // 3. Verifica se os dados do usuário no DTO estão corretos
        assertEquals(user.getUsername(), result.getUsername());
        
        // 4. Verifica se os métodos mockados foram chamados UMA vez
        verify(userRepository, times(1)).findByUsername(userLoginDTO.getUsername());
        verify(passwordEncoder, times(1)).matches(userLoginDTO.getPassword(), ENCODED_PASSWORD);
        verify(jwtService, times(1)).generateToken(user);
    }
    
    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não existe")
    void loginUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // ARRANGE
        // Configura o Repository para retornar Optional.empty()
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Verifica se a exceção correta é lançada
        assertThrows(UserNotFoundException.class, () -> {
            userService.loginUser(userLoginDTO);
        });

        // Verifica se o PasswordEncoder e o JwtService NÃO foram chamados
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    @DisplayName("Deve lançar InvalidPasswordException quando a senha estiver incorreta")
    void loginUser_ShouldThrowInvalidPasswordException_WhenPasswordIsIncorrect() {
        // ARRANGE
        // 1. Configura o Repository para encontrar o usuário
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        
        // 2. Configura o PasswordEncoder para retornar falso
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // ACT & ASSERT
        // Verifica se a exceção correta é lançada
        assertThrows(InvalidPasswordException.class, () -> {
            userService.loginUser(userLoginDTO);
        });

        // Verifica se o JwtService NÃO foi chamado
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
 // =============================================================================================================
 //   TESTANDO A MUDANÇA DE SENHA
 // =============================================================================================================
    		
    @Test
    @DisplayName("Deve alterar a senha com sucesso quando todas as validações passarem")
    void changePassword_ShouldSucceed_WhenValidationsPass() throws Exception {
        // ARRANGE
        Long userId = 1L;
        // 1. Simula a busca do usuário
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // 2. Simula que a senha atual está correta
        when(passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);
        // 3. Simula a codificação da nova senha
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("newHashedPassword456");

        // ACT
        userService.changePassword(userId, passwordChangeDTO);

        // ASSERT
        // Verifica se o repositório foi chamado para salvar o usuário com a nova senha
        verify(userRepository, times(1)).save(user);
        // Verifica se o PasswordEncoder foi chamado para codificar a nova senha
        verify(passwordEncoder, times(1)).encode(NEW_PASSWORD);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException se o ID do usuário não existir")
    void changePassword_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // ARRANGE
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> {
            userService.changePassword(userId, passwordChangeDTO);
        });

        // Garante que o encoder não foi chamado
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar InvalidPasswordException se a senha atual estiver incorreta")
    void changePassword_ShouldThrowInvalidPasswordException_WhenCurrentPasswordIsIncorrect() {
        // ARRANGE
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Simula que a senha atual fornecida está errada
        when(passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())).thenReturn(false);

        // ACT & ASSERT
        assertThrows(InvalidPasswordException.class, () -> {
            userService.changePassword(userId, passwordChangeDTO);
        });

        // Garante que a nova senha não foi codificada nem salva
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar InvalidConfirmationException se NewPassword e Confirmation forem diferentes")
    void changePassword_ShouldThrowInvalidConfirmationException_WhenNewPasswordAndConfirmationMismatch() {
        // ARRANGE
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Simula que a senha atual está correta para chegar na próxima validação
        when(passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);

        // Cria um DTO com confirmação errada APENAS para este teste
        PasswordChangeDTO mismatchDTO = new PasswordChangeDTO(
            "rawPassword", 
            NEW_PASSWORD, 
            "DifferentConfirmation" // Diferente da NewPassword
        );

        // ACT & ASSERT
        assertThrows(InvalidConfirmationException.class, () -> {
            userService.changePassword(userId, mismatchDTO);
        });

        // Garante que a nova senha não foi codificada nem salva
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
 // =============================================================================================================
 //    TESTANDO ACHAR O USUARIO
 // =============================================================================================================    
    
    @Test
    @DisplayName("Deve retornar o DTO de usuário quando o ID for encontrado")
    void findUserById_ShouldReturnUserResponseDTO_WhenIdExists() throws UserNotFoundException {
        // ARRANGE
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);
        
        // ACT
        UserResponseDTO result = userService.findUserById(userId);

        // ASSERT
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o ID não for encontrado")
    void findUserById_ShouldThrowUserNotFoundException_WhenIdDoesNotExist() {
        // ARRANGE
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(userId);
        });
        verify(userRepository, times(1)).findById(userId);
    }
}
