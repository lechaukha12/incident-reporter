package com.nganhang.sentinel.repository;

import com.nganhang.sentinel.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    
    // Native update query for problematic fields
    @Modifying
    @Query(value = "UPDATE incidents SET " +
                   "title = COALESCE(:title, title), " +
                   "description = COALESCE(:description, description), " +
                   "affected_service = COALESCE(:affectedService, affected_service), " +
                   "reported_by = COALESCE(:reportedBy, reported_by), " +
                   "updated_at = :updatedAt " +
                   "WHERE id = :id", nativeQuery = true)
    int updateProblematicFields(@Param("id") Long id, 
                               @Param("title") String title,
                               @Param("description") String description,
                               @Param("affectedService") String affectedService,
                               @Param("reportedBy") String reportedBy,
                               @Param("updatedAt") LocalDateTime updatedAt);

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
