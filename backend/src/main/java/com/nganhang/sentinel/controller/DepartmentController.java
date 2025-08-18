package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.model.Department;
import com.nganhang.sentinel.model.User;
import com.nganhang.sentinel.service.DepartmentService;
import com.nganhang.sentinel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllActiveDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        try {
            return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDepartment(
        @RequestBody Department department, 
        Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            Department createdDepartment = departmentService.createDepartment(department, currentUser.getId());
            return ResponseEntity.ok(createdDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
        @PathVariable Long id,
        @RequestBody Department departmentDetails,
        Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails, currentUser.getId());
            return ResponseEntity.ok(updatedDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id, Principal principal) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            departmentService.deleteDepartment(id, currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartments(@RequestParam(required = false) String keyword) {
        try {
            List<Department> departments = departmentService.searchDepartments(keyword);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
