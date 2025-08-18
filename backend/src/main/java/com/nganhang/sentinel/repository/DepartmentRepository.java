package com.nganhang.sentinel.repository;

import com.nganhang.sentinel.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByIsActiveTrueOrderByNameAsc();
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND d.managerId = :managerId")
    List<Department> findByManagerId(@Param("managerId") Long managerId);
    
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
    
    @Query("SELECT d FROM Department d WHERE d.isActive = true AND (:managerId IS NULL OR d.managerId = :managerId)")
    List<Department> findActiveDepartments(@Param("managerId") Long managerId);
}
