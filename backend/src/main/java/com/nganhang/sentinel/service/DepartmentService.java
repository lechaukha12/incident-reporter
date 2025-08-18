package com.nganhang.sentinel.service;

import com.nganhang.sentinel.model.Department;
import com.nganhang.sentinel.model.User;
import com.nganhang.sentinel.repository.DepartmentRepository;
import com.nganhang.sentinel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    
    public List<Department> getAllActiveDepartments() {
        return departmentRepository.findByIsActiveTrueOrderByNameAsc();
    }
    
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    public Department createDepartment(Department department, Long createdBy) {
        // Check if user is admin
        User creator = userRepository.findById(createdBy)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!"ADMIN".equals(creator.getRole())) {
            throw new RuntimeException("Only admin can create departments");
        }
        
        // Check if department name already exists
        if (departmentRepository.existsByNameIgnoreCase(department.getName())) {
            throw new RuntimeException("Department name already exists");
        }
        
        department.setCreatedBy(createdBy);
        department.setIsActive(true);
        
        return departmentRepository.save(department);
    }
    
    public Department updateDepartment(Long id, Department departmentDetails, Long updatedBy) {
        // Check if user is admin
        User updater = userRepository.findById(updatedBy)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!"ADMIN".equals(updater.getRole())) {
            throw new RuntimeException("Only admin can update departments");
        }
        
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        // Check if new name conflicts with existing department
        if (!department.getName().equalsIgnoreCase(departmentDetails.getName()) &&
            departmentRepository.existsByNameIgnoreCase(departmentDetails.getName())) {
            throw new RuntimeException("Department name already exists");
        }
        
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        department.setManagerId(departmentDetails.getManagerId());
        
        return departmentRepository.save(department);
    }
    
    public void deleteDepartment(Long id, Long deletedBy) {
        // Check if user is admin
        User deleter = userRepository.findById(deletedBy)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!"ADMIN".equals(deleter.getRole())) {
            throw new RuntimeException("Only admin can delete departments");
        }
        
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found"));
        
        // Soft delete
        department.setIsActive(false);
        departmentRepository.save(department);
    }
    
    public List<Department> searchDepartments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveDepartments();
        }
        return departmentRepository.findByNameContainingIgnoreCase(keyword.trim());
    }
}
