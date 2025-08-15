package com.nganhang.sentinel.service;

import com.nganhang.sentinel.dto.LoginRequestDTO;
import com.nganhang.sentinel.dto.LoginResponseDTO;
import com.nganhang.sentinel.dto.UserResponseDTO;
import com.nganhang.sentinel.model.User;
import com.nganhang.sentinel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Simple in-memory token storage (use Redis in production)
    private Map<String, Long> tokenStore = new HashMap<>();

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Simple password check (use BCrypt in production)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        // Build response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setSuccess(true);
        response.setMessage("Đăng nhập thành công");
        response.setToken(token);
        
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        userDTO.setActive(user.isActive());
        
        response.setUser(userDTO);
        return response;
    }

    public void logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenStore.remove(token);
    }

    public LoginResponseDTO getCurrentUser(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = tokenStore.get(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponseDTO response = new LoginResponseDTO();
        response.setSuccess(true);
        
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        userDTO.setActive(user.isActive());
        
        response.setUser(userDTO);
        return response;
    }

    public boolean isValidToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenStore.containsKey(token);
    }
}
