package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.LoginRequestDTO;
import com.nganhang.sentinel.dto.LoginResponseDTO;
import com.nganhang.sentinel.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Tên đăng nhập hoặc mật khẩu không đúng");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            LoginResponseDTO response = authService.getCurrentUser(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
