package com.project.tarefas.config.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.tarefas.model.User;

public class SecurityUserDetails implements UserDetails {

    // 1. Mantenha a referência à Entidade de Domínio
    private final User user; 

    // 2. Construtor que recebe a Entidade User
    public SecurityUserDetails(User user) {
        this.user = user;
    }

    

    // --- Métodos de Autoridade ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se você tiver roles na Entidade User, adapte aqui.
        // Exemplo: new SimpleGrantedAuthority(user.getRole());
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // --- Métodos de Credenciais (Delegam à Entidade) ---
    @Override
    public String getPassword() {
        return user.getPassword(); // Pega a senha diretamente da Entidade
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Pega o username diretamente da Entidade
    }

    // --- Métodos de Status (Geralmente true para REST APIs) ---

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}