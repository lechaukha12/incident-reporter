package com.nganhang.sentinel.service;

import com.nganhang.sentinel.dto.UserCreateDTO;
import com.nganhang.sentinel.dto.UserResponseDTO;
import com.nganhang.sentinel.dto.UserUpdateDTO;
import com.nganhang.sentinel.model.User;
import com.nganhang.sentinel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Note: In a real app, you'd inject PasswordEncoder
    // @Autowired
    // private PasswordEncoder passwordEncoder;
    
    // Get all users
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    // Get user by ID
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDTO::new);
    }
    
    // Get user by username
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseDTO::new);
    }
    
    // Find user entity by username (for internal use)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    // Create new user
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(userCreateDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + userCreateDTO.getUsername());
        }
        
        // Check if email already exists
        if (userCreateDTO.getEmail() != null && userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + userCreateDTO.getEmail());
        }
        
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        // In real app: user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setPassword(userCreateDTO.getPassword()); // Plain text for demo
        user.setFullName(userCreateDTO.getFullName());
        user.setEmail(userCreateDTO.getEmail());
        user.setPhoneNumber(userCreateDTO.getPhoneNumber());
        user.setDepartment(userCreateDTO.getDepartment());
        
        // Set role
        if (userCreateDTO.getRole() != null) {
            user.setRole(User.UserRole.valueOf(userCreateDTO.getRole()));
        }
        
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }
    
    // Update user
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Check email uniqueness if changing
        if (userUpdateDTO.getEmail() != null && 
            !userUpdateDTO.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(userUpdateDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + userUpdateDTO.getEmail());
        }
        
        // Update fields
        if (userUpdateDTO.getFullName() != null) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if (userUpdateDTO.getDepartment() != null) {
            user.setDepartment(userUpdateDTO.getDepartment());
        }
        if (userUpdateDTO.getRole() != null) {
            user.setRole(User.UserRole.valueOf(userUpdateDTO.getRole()));
        }
        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(User.UserStatus.valueOf(userUpdateDTO.getStatus()));
        }
        
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    // Search users
    public List<UserResponseDTO> searchUsers(String searchTerm) {
        return userRepository.findBySearchTerm(searchTerm).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    // Get users by role
    public List<UserResponseDTO> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    // Get users by status
    public List<UserResponseDTO> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    // Get users by department
    public List<UserResponseDTO> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department).stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    // Get all departments
    public List<String> getAllDepartments() {
        return userRepository.findAllDepartments();
    }
    
    // Update last login
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userRepository.save(user);
                });
    }
    
    // Change password
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // In real app: user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword); // Plain text for demo
        userRepository.save(user);
    }
    
    // Toggle user status
    public UserResponseDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        user.setStatus(user.getStatus() == User.UserStatus.ACTIVE ? 
                      User.UserStatus.INACTIVE : User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }
}
