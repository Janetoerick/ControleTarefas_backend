package com.project.tarefas.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.tarefas.model.User;
import com.project.tarefas.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final UserRepository userRepository;
	
    public SecurityConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
    	 http
         .csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
             .requestMatchers("/api/auth/**").permitAll()
             .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
             .requestMatchers("/api/**").permitAll() // PERMITE ACESSO TOTAL PARA TESTES
             .anyRequest().authenticated()
         )
         .sessionManagement(session -> session
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         )
         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
     
     return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
            
            // Use o seu arquivo que já existe!
            return new SecurityUserDetails(user);
        };
    }
}
