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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentUpdateRepository incidentUpdateRepository;
    
    @Transactional
    public IncidentResponseDTO createIncident(IncidentCreateDTO dto) {
        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Incident.IncidentStatus.INVESTIGATING) // Mặc định khi tạo mới
                .severityLevel(dto.getSeverityLevel())
                .affectedService(dto.getAffectedService())
                .assignee(dto.getAssignee())
                .reportedBy(dto.getReportedBy())
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
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        boolean statusChanged = dto.getStatus() != null && dto.getStatus() != incident.getStatus();
        boolean severityChanged = dto.getSeverityLevel() != null && dto.getSeverityLevel() != incident.getSeverityLevel();
        boolean assigneeChanged = dto.getAssignee() != null && !dto.getAssignee().equals(incident.getAssignee());
        boolean resolvedChanged = dto.isResolved() != incident.isResolved();
        
        // Cập nhật thông tin sự cố
        if (dto.getStatus() != null) {
            incident.setStatus(dto.getStatus());
        }
        
        if (dto.getSeverityLevel() != null) {
            incident.setSeverityLevel(dto.getSeverityLevel());
        }
        
        if (dto.getAssignee() != null) {
            incident.setAssignee(dto.getAssignee());
        }
        
        if (resolvedChanged && dto.isResolved()) {
            incident.setResolved(true);
            incident.setResolvedAt(LocalDateTime.now());
            incident.setStatus(Incident.IncidentStatus.RESOLVED);
        } else if (resolvedChanged) {
            incident.setResolved(false);
            incident.setResolvedAt(null);
        }
        
        Incident updatedIncident = incidentRepository.save(incident);
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(updatedIncident);
        
        return mapToResponseDTO(updatedIncident, updates);
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
