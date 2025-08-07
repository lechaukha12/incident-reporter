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

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("incidentServiceV3")
@RequiredArgsConstructor
public class IncidentServiceV3 {

    private static final Logger logger = LoggerFactory.getLogger(IncidentServiceV3.class);
    
    private final IncidentRepository incidentRepository;
    private final IncidentUpdateRepository incidentUpdateRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public IncidentResponseDTO createIncident(IncidentCreateDTO dto) {
        logger.info("Creating incident with title: {}", dto.getTitle());
        
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

    @Transactional
    public IncidentResponseDTO updateIncident(Long id, IncidentUpdateDTO dto) {
        logger.info("=== V3 UPDATE START === ID: {} with title: {}, description: {}", 
                   id, dto.getTitle(), dto.getDescription());
        
        // Fetch incident with explicit refresh
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sự cố với ID: " + id));
        
        logger.info("BEFORE UPDATE - Title: '{}', Description: '{}'", 
                   incident.getTitle(), incident.getDescription());
        
        // Clear any potential cache
        entityManager.detach(incident);
        incident = entityManager.find(Incident.class, id);
        
        // DIRECT field updates with explicit logging
        String oldTitle = incident.getTitle();
        String oldDescription = incident.getDescription();
        
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            incident.setTitle(dto.getTitle().trim());
            logger.info("SET TITLE from '{}' to '{}'", oldTitle, incident.getTitle());
        }
        
        if (dto.getDescription() != null) {
            String newDesc = dto.getDescription().trim().isEmpty() ? null : dto.getDescription().trim();
            incident.setDescription(newDesc);
            logger.info("SET DESCRIPTION from '{}' to '{}'", oldDescription, incident.getDescription());
        }
        
        if (dto.getAffectedService() != null) {
            incident.setAffectedService(dto.getAffectedService().trim().isEmpty() ? null : dto.getAffectedService().trim());
        }
        
        if (dto.getReportedBy() != null && !dto.getReportedBy().trim().isEmpty()) {
            incident.setReportedBy(dto.getReportedBy().trim());
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
            incident.setAssignee(dto.getAssignee().trim().isEmpty() ? null : dto.getAssignee().trim());
        }
        
        if (dto.getNotes() != null) {
            incident.setNotes(dto.getNotes().trim().isEmpty() ? null : dto.getNotes().trim());
        }
        
        if (dto.getRootCause() != null) {
            incident.setRootCause(dto.getRootCause().trim().isEmpty() ? null : dto.getRootCause().trim());
        }
        
        // Force flush and refresh
        logger.info("SAVING incident with Title: '{}', Description: '{}'", 
                   incident.getTitle(), incident.getDescription());
        
        Incident savedIncident = incidentRepository.saveAndFlush(incident);
        entityManager.refresh(savedIncident);
        
        logger.info("AFTER SAVE & REFRESH - Title: '{}', Description: '{}'", 
                   savedIncident.getTitle(), savedIncident.getDescription());
        
        // Verify in database immediately
        Incident dbIncident = entityManager.createQuery(
            "SELECT i FROM Incident i WHERE i.id = :id", Incident.class)
            .setParameter("id", id)
            .getSingleResult();
            
        logger.info("DIRECT DB QUERY - Title: '{}', Description: '{}'", 
                   dbIncident.getTitle(), dbIncident.getDescription());
        
        List<IncidentUpdate> updates = incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(savedIncident);
        return mapToResponseDTO(savedIncident, updates);
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
