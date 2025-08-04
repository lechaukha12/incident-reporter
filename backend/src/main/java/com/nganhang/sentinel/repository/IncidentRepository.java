package com.nganhang.sentinel.repository;

import com.nganhang.sentinel.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByStatus(Incident.IncidentStatus status, Pageable pageable);
    
    Page<Incident> findBySeverityLevel(Incident.SeverityLevel severityLevel, Pageable pageable);
    
    Page<Incident> findByAssignee(String assignee, Pageable pageable);
    
    Page<Incident> findByResolvedTrue(Pageable pageable);
    
    Page<Incident> findByResolvedFalse(Pageable pageable);
    
    Page<Incident> findByAffectedServiceContainingIgnoreCase(String service, Pageable pageable);
    
    Page<Incident> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword, String descriptionKeyword, Pageable pageable);
    
    List<Incident> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
