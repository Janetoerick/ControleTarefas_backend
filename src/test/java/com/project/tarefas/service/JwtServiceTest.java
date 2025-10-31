package com.project.tarefas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.tarefas.config.security.SecurityUserDetails;
import com.project.tarefas.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

 private JwtService jwtService;
 
 private final String SECRET_KEY = "SuaChaveSecretaMuitoLongaEComplexaParaTestes1234567890";
 private final long EXPIRATION_TIME_MS = 1000L; // 1 segundo para o teste de expiração

 private UserDetails userDetails;

 @BeforeEach
 void setUp() {
	 jwtService = new JwtService(SECRET_KEY, EXPIRATION_TIME_MS);
	 
	 User user = new User(1L, "TestUser", "test@mail.com", "hashedPassword", "ROLE_USER");
	 userDetails = new SecurityUserDetails(user);
 }

 // =================================================================
 // TESTES DE GERAÇÃO E EXTRAÇÃO
 // =================================================================

 @Test
 @DisplayName("Deve gerar um token e extrair o username corretamente")
 void generateToken_ShouldExtractUsernameCorrectly() {
     // ACT
     String token = jwtService.generateToken(userDetails);
     
     // ASSERT
     assertNotNull(token);
     assertFalse(token.isEmpty());
     
     // Verifica se o username extraído corresponde ao usuário original
     String extractedUsername = jwtService.extractUsername(token);
     assertEquals(userDetails.getUsername(), extractedUsername);
 }
 
 // =================================================================
 // TESTES DE VALIDAÇÃO
 // =================================================================

 @Test
 @DisplayName("Deve validar o token gerado para o usuário correto")
 void isTokenValid_ShouldReturnTrue_ForValidToken() {
     // ARRANGE
     String token = jwtService.generateToken(userDetails);
     
     // ACT & ASSERT
     assertTrue(jwtService.isTokenValid(token, userDetails));
 }

 @Test
 @DisplayName("Deve retornar falso para um token com usuário diferente")
 void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
     // ARRANGE
     String token = jwtService.generateToken(userDetails);
     
     // Cria um usuário diferente
     User userEntidadeDiferente = new User(2L, "DifferentUser", "other@mail.com", "otherHash", "ROLE_USER");
     UserDetails userDiferenteAdaptado = new SecurityUserDetails(userEntidadeDiferente);

     // ACT & ASSERT
     assertFalse(jwtService.isTokenValid(token, userDiferenteAdaptado));
 }

 @Test
 @DisplayName("Deve retornar falso para um token expirado")
 void isTokenValid_ShouldReturnFalse_ForExpiredToken() throws InterruptedException {
     // ARRANGE
     // Gera o token (com expiração de 1000ms, conforme TestPropertySource)
     String token = jwtService.generateToken(userDetails);
     
     // Aguarda mais de 1 segundo para garantir que o token expirou
     Thread.sleep(1500); 

     // ACT & ASSERT
     assertFalse(jwtService.isTokenValid(token, userDetails));
 }
 
 // =================================================================
 // TESTE DE FALHA DE ASSINATURA (Authentication Failure)
 // =================================================================
 
 @Test
 @DisplayName("Deve falhar na validação se a assinatura foi alterada (chave secreta diferente)")
 void isTokenValid_ShouldReturnFalse_ForInvalidSignature() {
     // ARRANGE
     
     // 1. Gera um token VÁLIDO usando a CHAVE SECRETA DO TESTE (a injetada no jwtService)
     String validToken = jwtService.generateToken(userDetails);
     
     // 2. CRIA UMA CHAVE SECRETA FALSA (diferente da chave real)
     String MOCK_SECRET = "EstaChaveSecretaECompletamenteDiferenteDaOriginal1234567890";
     Key fakeKey = Keys.hmacShaKeyFor(MOCK_SECRET.getBytes(StandardCharsets.UTF_8));

     // 3. RECRIAR (OU FORJAR) O TOKEN USANDO A CHAVE FALSA
     String forgedToken = Jwts.builder()
         .subject(userDetails.getUsername())
         .issuedAt(new Date(System.currentTimeMillis()))
         .expiration(new Date(System.currentTimeMillis() + 60000)) // 1 minuto
         .signWith(fakeKey) // Assinado com a CHAVE FALSA
         .compact();

     // ACT & ASSERT
     assertFalse(jwtService.isTokenValid(forgedToken, userDetails), 
                 "A validação deve falhar para tokens assinados com chave incorreta.");
 }
}
