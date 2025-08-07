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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cleaned up version of IncidentService
 * Removes all workarounds and debugging code
 * Focus on simple, clean JPA operations
 */
@Service("incidentServiceV2")
@RequiredArgsConstructor
public class IncidentServiceV2 {
    
    private static final Logger logger = LoggerFactory.getLogger(IncidentServiceV2.class);
    
    private final IncidentRepository incidentRepository;
    private final IncidentUpdateRepository incidentUpdateRepository;
    
    @Transactional
    public IncidentResponseDTO createIncident(IncidentCreateDTO dto) {
        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Incident.IncidentStatus.INVESTIGATING)
                .severityLevel(dto.getSeverityLevel())
                .affectedService(dto.getAffectedService())
                .assignee(dto.getAssignee())
                .reportedBy(dto.getReportedBy())
                .notes(dto.getNotes())
                .rootCause(dto.getRootCause())
                .resolved(false)
                .build();
        
        Incident savedIncident = incidentRepository.save(incident);
        logger.info("Created incident with ID: {}", savedIncident.getId());
        
        // Create initial timeline entry
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
    public IncidentResponseDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(incident);
        return mapToResponseDTO(incident, updates);
    }
    
    @Transactional
    public IncidentResponseDTO updateIncident(Long id, IncidentUpdateDTO dto) {
        logger.info("Updating incident ID: {} with title: {}", id, dto.getTitle());
        
        // Fetch incident
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        logger.info("Current incident title: {}, description: {}", incident.getTitle(), incident.getDescription());
        
        // Update fields directly - no complex logic
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            incident.setTitle(dto.getTitle().trim());
        }
        
        if (dto.getDescription() != null) {
            incident.setDescription(dto.getDescription().trim().isEmpty() ? null : dto.getDescription().trim());
        }
        
        if (dto.getAffectedService() != null) {
            incident.setAffectedService(dto.getAffectedService().trim().isEmpty() ? null : dto.getAffectedService().trim());
        }
        
        if (dto.getReportedBy() != null && !dto.getReportedBy().trim().isEmpty()) {
            incident.setReportedBy(dto.getReportedBy().trim());
        }
        
        if (dto.getStatus() != null) {
            incident.setStatus(dto.getStatus());
            // Auto-update resolved status
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
            incident.setAssignee(dto.getAssignee().trim().isEmpty() ? null : dto.getAssignee().trim());
        }
        
        if (dto.getNotes() != null) {
            incident.setNotes(dto.getNotes().trim().isEmpty() ? null : dto.getNotes().trim());
        }
        
        if (dto.getRootCause() != null) {
            incident.setRootCause(dto.getRootCause().trim().isEmpty() ? null : dto.getRootCause().trim());
        }
        
        // Save and return
        Incident updatedIncident = incidentRepository.save(incident);
        logger.info("Updated incident title: {}, description: {}", updatedIncident.getTitle(), updatedIncident.getDescription());
        
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
                .notes(incident.getNotes())
                .rootCause(incident.getRootCause())
                .timeline(timelineUpdates)
                .build();
    }
}
