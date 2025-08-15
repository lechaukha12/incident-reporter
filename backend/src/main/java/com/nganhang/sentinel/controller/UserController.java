package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.UserCreateDTO;
import com.nganhang.sentinel.dto.UserResponseDTO;
import com.nganhang.sentinel.dto.UserUpdateDTO;
import com.nganhang.sentinel.model.User;
import com.nganhang.sentinel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            UserResponseDTO createdUser = userService.createUser(userCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id, 
            @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String q) {
        List<UserResponseDTO> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
    
    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<UserResponseDTO> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get users by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByStatus(@PathVariable String status) {
        try {
            User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
            List<UserResponseDTO> users = userService.getUsersByStatus(userStatus);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get users by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByDepartment(@PathVariable String department) {
        List<UserResponseDTO> users = userService.getUsersByDepartment(department);
        return ResponseEntity.ok(users);
    }
    
    // Get all departments
    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        List<String> departments = userService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
    
    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Change password
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            userService.changePassword(id, newPassword);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Toggle user status
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable Long id) {
        try {
            UserResponseDTO updatedUser = userService.toggleUserStatus(id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Update last login
    @PutMapping("/{username}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable String username) {
        userService.updateLastLogin(username);
        return ResponseEntity.ok().build();
    }
}
