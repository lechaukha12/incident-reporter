package com.nganhang.sentinel.service;

import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentResponseDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.model.IncidentUpdate;
import com.nganhang.sentinel.repository.IncidentRepository;
import com.nganhang.sentinel.repository.IncidentUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IncidentService {
    
    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);
    
    @Autowired
    private IncidentRepository incidentRepository;
    
    @Autowired
    private IncidentUpdateRepository incidentUpdateRepository;
    
    @Autowired
    private EntityManager entityManager;    @Transactional
    public IncidentResponseDTO createIncident(IncidentCreateDTO dto) {
        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Incident.IncidentStatus.INVESTIGATING) // Mặc định khi tạo mới
                .severityLevel(dto.getSeverityLevel())
                .affectedService(dto.getAffectedService())
                .assignee(dto.getAssignee())
                .reportedBy(dto.getReportedBy())
                .notes(dto.getNotes())
                .rootCause(dto.getRootCause())
                .resolved(false)
                .build();
        
        Incident savedIncident = incidentRepository.save(incident);
        
        // Tạo bản ghi đầu tiên trong timeline
        IncidentUpdate initialUpdate = IncidentUpdate.builder()
                .incident(savedIncident)
                .message("Sự cố được tạo và đang trong quá trình điều tra")
                .updateType(IncidentUpdate.UpdateType.STATUS_CHANGE)
                .createdBy(dto.getReportedBy())
                .build();
        
        incidentUpdateRepository.save(initialUpdate);
        
        return mapToResponseDTO(savedIncident, List.of(initialUpdate));
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getIncidentsByStatus(Incident.IncidentStatus status, Pageable pageable) {
        return incidentRepository.findByStatus(status, pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getIncidentsBySeverity(Incident.SeverityLevel severityLevel, Pageable pageable) {
        return incidentRepository.findBySeverityLevel(severityLevel, pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getIncidentsByAssignee(String assignee, Pageable pageable) {
        return incidentRepository.findByAssignee(assignee, pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getIncidentsByResolved(boolean resolved, Pageable pageable) {
        Page<Incident> incidents = resolved ? 
                incidentRepository.findByResolvedTrue(pageable) : 
                incidentRepository.findByResolvedFalse(pageable);
                
        return incidents.map(incident -> {
            List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
            return mapToResponseDTO(incident, updates);
        });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getIncidentsByService(String service, Pageable pageable) {
        return incidentRepository.findByAffectedServiceContainingIgnoreCase(service, pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> searchIncidents(String query, Pageable pageable) {
        return incidentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
                .map(incident -> {
                    List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
                    return mapToResponseDTO(incident, updates);
                });
    }
    
    @Transactional(readOnly = true)
    public IncidentResponseDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
        
        return mapToResponseDTO(incident, updates);
    }
    
    @Transactional
    public IncidentResponseDTO updateIncident(Long id, IncidentUpdateDTO dto) {
        System.out.println("=== updateIncident called with id=" + id + " ===");
        logger.info("=== updateIncident called with id={} ===", id);
        
        logger.info("SERVICE: Received DTO for incident {}: title={}, description={}, affectedService={}, reportedBy={}", 
                id, dto.getTitle(), dto.getDescription(), dto.getAffectedService(), dto.getReportedBy());
        
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        logger.info("SERVICE: Before update - title={}, description={}, affectedService={}, reportedBy={}", 
                incident.getTitle(), incident.getDescription(), incident.getAffectedService(), incident.getReportedBy());
        
        boolean statusChanged = dto.getStatus() != null && dto.getStatus() != incident.getStatus();
        boolean severityChanged = dto.getSeverityLevel() != null && dto.getSeverityLevel() != incident.getSeverityLevel();
        boolean assigneeChanged = dto.getAssignee() != null && !dto.getAssignee().equals(incident.getAssignee());
        boolean resolvedChanged = dto.isResolved() != incident.isResolved();
        
        // Debug log to file
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/debug.log"), 
                ("DTO VALUES: title=" + dto.getTitle() + 
                 ", description=" + dto.getDescription() + 
                 ", affectedService=" + dto.getAffectedService() + 
                 ", reportedBy=" + dto.getReportedBy() + "\n").getBytes(),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) { /* ignore */ }

        // WORKAROUND: Use native query for problematic fields if JPA doesn't work
        boolean needsNativeUpdate = false;
        String nativeTitle = null, nativeDescription = null, nativeAffectedService = null, nativeReportedBy = null;
        
        // Cập nhật các trường thông tin cơ bản - loại bỏ điều kiện .trim().isEmpty()
        if (dto.getTitle() != null) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/debug.log"), 
                    ("Setting title: BEFORE=" + incident.getTitle() + " TO=" + dto.getTitle() + "\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception e) { /* ignore */ }
            System.out.println("SERVICE DEBUG - Setting title from '" + incident.getTitle() + "' to '" + dto.getTitle() + "'");
            incident.setTitle(dto.getTitle());
            System.out.println("SERVICE DEBUG - Title after set: '" + incident.getTitle() + "'");
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("/tmp/debug.log"), 
                    ("AFTER SET title=" + incident.getTitle() + "\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception e) { /* ignore */ }
            needsNativeUpdate = true;
            nativeTitle = dto.getTitle();
        }
        
        if (dto.getDescription() != null) {
            System.out.println("SERVICE DEBUG - Setting description from '" + incident.getDescription() + "' to '" + dto.getDescription() + "'");
            incident.setDescription(dto.getDescription());
            System.out.println("SERVICE DEBUG - Description after set: '" + incident.getDescription() + "'");
        }
        
        if (dto.getAffectedService() != null) {
            System.out.println("SERVICE DEBUG - Setting affectedService from '" + incident.getAffectedService() + "' to '" + dto.getAffectedService() + "'");
            incident.setAffectedService(dto.getAffectedService());
            System.out.println("SERVICE DEBUG - AffectedService after set: '" + incident.getAffectedService() + "'");
        }
        
        if (dto.getReportedBy() != null) {
            System.out.println("SERVICE DEBUG - Setting reportedBy from '" + incident.getReportedBy() + "' to '" + dto.getReportedBy() + "'");
            incident.setReportedBy(dto.getReportedBy());
            System.out.println("SERVICE DEBUG - ReportedBy after set: '" + incident.getReportedBy() + "'");
        }
        
        // Cập nhật thông tin sự cố
        if (dto.getStatus() != null) {
            incident.setStatus(dto.getStatus());
            // Tự động cập nhật trạng thái resolved dựa trên status
            if (dto.getStatus() == Incident.IncidentStatus.RESOLVED) {
                incident.setResolved(true);
                incident.setResolvedAt(LocalDateTime.now());
            } else {
                incident.setResolved(false);
                incident.setResolvedAt(null);
            }
        }
        
        if (dto.getSeverityLevel() != null) {
            incident.setSeverityLevel(dto.getSeverityLevel());
        }
        
        if (dto.getAssignee() != null) {
            incident.setAssignee(dto.getAssignee());
        }
        
        // Xử lý trường hợp cập nhật trực tiếp trường resolved (nếu có)
        if (resolvedChanged && dto.getStatus() == null) {
            if (dto.isResolved()) {
                incident.setResolved(true);
                incident.setResolvedAt(LocalDateTime.now());
                incident.setStatus(Incident.IncidentStatus.RESOLVED);
            } else {
                incident.setResolved(false);
                incident.setResolvedAt(null);
            }
        }
        
        // Cập nhật notes và rootCause nếu có
        if (dto.getNotes() != null) {
            incident.setNotes(dto.getNotes());
        }
        
        if (dto.getRootCause() != null) {
            incident.setRootCause(dto.getRootCause());
        }
        
        // Cập nhật thời gian chỉnh sửa
        incident.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("SERVICE DEBUG - Before save: Title='" + incident.getTitle() + "' Description='" + incident.getDescription() + "'");
        
        // FORCE JPA TO FLUSH - Clear persistence context and reload entity
        incidentRepository.flush();
        entityManager.clear(); // Clear persistence context
        
        // Reload entity fresh from database
        incident = incidentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Incident not found"));
        
        // Re-apply ALL changes after reload
        if (dto.getTitle() != null) {
            System.out.println("FORCE: Setting title from '" + incident.getTitle() + "' to '" + dto.getTitle() + "'");
            incident.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            System.out.println("FORCE: Setting description from '" + incident.getDescription() + "' to '" + dto.getDescription() + "'");
            incident.setDescription(dto.getDescription());
        }
        if (dto.getAffectedService() != null) {
            System.out.println("FORCE: Setting affectedService from '" + incident.getAffectedService() + "' to '" + dto.getAffectedService() + "'");
            incident.setAffectedService(dto.getAffectedService());
        }
        if (dto.getReportedBy() != null) {
            System.out.println("FORCE: Setting reportedBy from '" + incident.getReportedBy() + "' to '" + dto.getReportedBy() + "'");
            incident.setReportedBy(dto.getReportedBy());
        }
        if (dto.getStatus() != null) {
            incident.setStatus(dto.getStatus());
            if (dto.getStatus() == Incident.IncidentStatus.RESOLVED) {
                incident.setResolved(true);
                incident.setResolvedAt(LocalDateTime.now());
            } else {
                incident.setResolved(false);
                incident.setResolvedAt(null);
            }
        }
        if (dto.getSeverityLevel() != null) {
            incident.setSeverityLevel(dto.getSeverityLevel());
        }
        if (dto.getAssignee() != null) {
            incident.setAssignee(dto.getAssignee());
        }
        if (dto.getNotes() != null) {
            incident.setNotes(dto.getNotes());
        }
        if (dto.getRootCause() != null) {
            incident.setRootCause(dto.getRootCause());
        }
        
        // Set updated timestamp
        incident.setUpdatedAt(LocalDateTime.now());
        
        Incident updatedIncident;
        try {
            updatedIncident = incidentRepository.save(incident);
            System.out.println("SERVICE DEBUG - After save: Title='" + updatedIncident.getTitle() + "' Description='" + updatedIncident.getDescription() + "'");
            
            // WORKAROUND: Always use repository native query for these problematic fields
            System.out.println("WORKAROUND: Using repository native SQL to update problematic fields");
            logger.info("WORKAROUND: Using repository native SQL to update problematic fields");
            int rowsUpdated = incidentRepository.updateProblematicFields(
                id, 
                dto.getTitle(),
                dto.getDescription(), 
                dto.getAffectedService(),
                dto.getReportedBy(),
                LocalDateTime.now()
            );
            System.out.println("WORKAROUND: Updated " + rowsUpdated + " rows");
            logger.info("WORKAROUND: Updated {} rows", rowsUpdated);
                
            // Refresh entity after native update
            entityManager.flush();
            entityManager.clear();
            updatedIncident = incidentRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            System.out.println("WORKAROUND ERROR: " + e.getMessage());
            logger.error("WORKAROUND ERROR: ", e);
            throw e;
        }
        
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(updatedIncident);
        
        IncidentResponseDTO result = mapToResponseDTO(updatedIncident, updates);
        System.out.println("SERVICE DEBUG - Final DTO: Title='" + result.getTitle() + "' Description='" + result.getDescription() + "'");
        
        return result;
    }
    
    @Transactional
    public IncidentResponseDTO addUpdate(Long incidentId, TimelineUpdateDTO dto) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + incidentId));
        
        IncidentUpdate update = IncidentUpdate.builder()
                .incident(incident)
                .message(dto.getMessage())
                .updateType(dto.getUpdateType())
                .createdBy(dto.getCreatedBy())
                .build();
        
        incidentUpdateRepository.save(update);
        
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
        
        return mapToResponseDTO(incident, updates);
    }
    
    private IncidentResponseDTO mapToResponseDTO(Incident incident, List<IncidentUpdate> updates) {
        List<IncidentResponseDTO.IncidentUpdateResponseDTO> timelineUpdates = updates.stream()
                .map(update -> IncidentResponseDTO.IncidentUpdateResponseDTO.builder()
                        .id(update.getId())
                        .message(update.getMessage())
                        .updateType(update.getUpdateType())
                        .createdBy(update.getCreatedBy())
                        .createdAt(update.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return IncidentResponseDTO.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .status(incident.getStatus())
                .severityLevel(incident.getSeverityLevel())
                .affectedService(incident.getAffectedService())
                .assignee(incident.getAssignee())
                .reportedBy(incident.getReportedBy())
                .resolved(incident.isResolved())
                .resolvedAt(incident.getResolvedAt())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .notes(incident.getNotes())
                .rootCause(incident.getRootCause())
                .timeline(timelineUpdates)
                .build();
    }
    
    // Thống kê
    @Transactional(readOnly = true)
    public long countTotalIncidents() {
        return incidentRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countActiveIncidents() {
        return incidentRepository.findByResolvedFalse(Pageable.unpaged()).getTotalElements();
    }
    
    @Transactional(readOnly = true)
    public long countResolvedIncidents() {
        return incidentRepository.findByResolvedTrue(Pageable.unpaged()).getTotalElements();
    }
    
    @Transactional(readOnly = true)
    public Map<Incident.SeverityLevel, Long> countBySeverity() {
        Map<Incident.SeverityLevel, Long> result = new HashMap<>();
        for (Incident.SeverityLevel level : Incident.SeverityLevel.values()) {
            result.put(level, incidentRepository.findBySeverityLevel(level, Pageable.unpaged()).getTotalElements());
        }
        return result;
    }
    
    @Transactional(readOnly = true)
    public Map<Incident.IncidentStatus, Long> countByStatus() {
        Map<Incident.IncidentStatus, Long> result = new HashMap<>();
        for (Incident.IncidentStatus status : Incident.IncidentStatus.values()) {
            result.put(status, incidentRepository.findByStatus(status, Pageable.unpaged()).getTotalElements());
        }
        return result;
    }
    
    // Xử lý webhook
    public IncidentCreateDTO convertWebhookToIncidentDTO(Map<String, Object> webhookData) {
        // Xử lý dữ liệu từ các hệ thống giám sát như Grafana, Prometheus
        // Đây là logic mẫu, cần điều chỉnh tùy theo định dạng webhook thực tế
        String title = (String) webhookData.getOrDefault("title", "Incident từ hệ thống giám sát");
        String description = (String) webhookData.getOrDefault("description", "");
        
        // Xác định mức độ nghiêm trọng dựa trên dữ liệu webhook
        Incident.SeverityLevel severity;
        String severityStr = (String) webhookData.getOrDefault("severity", "medium");
        switch (severityStr.toLowerCase()) {
            case "critical":
                severity = Incident.SeverityLevel.CRITICAL;
                break;
            case "high":
                severity = Incident.SeverityLevel.HIGH;
                break;
            case "medium":
                severity = Incident.SeverityLevel.MEDIUM;
                break;
            default:
                severity = Incident.SeverityLevel.LOW;
                break;
        }
        
        String service = (String) webhookData.getOrDefault("service", "");
        String reportedBy = (String) webhookData.getOrDefault("reportedBy", "Hệ thống");
        
        return IncidentCreateDTO.builder()
                .title(title)
                .description(description)
                .severityLevel(severity)
                .affectedService(service)
                .reportedBy(reportedBy)
                .build();
    }
}
