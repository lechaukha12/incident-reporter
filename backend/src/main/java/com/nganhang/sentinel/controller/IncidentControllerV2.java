package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentResponseDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.service.IncidentServiceV2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Clean version of IncidentController
 * Uses the refactored IncidentServiceV2
 */
@RestController
@RequestMapping("/api/v2/incidents")
@RequiredArgsConstructor
public class IncidentControllerV2 {
    
    private static final Logger logger = LoggerFactory.getLogger(IncidentControllerV2.class);
    
    private final IncidentServiceV2 incidentServiceV2;
    
    @PostMapping
    public ResponseEntity<IncidentResponseDTO> createIncident(@Valid @RequestBody IncidentCreateDTO dto) {
        logger.info("Creating incident with title: {}", dto.getTitle());
        IncidentResponseDTO result = incidentServiceV2.createIncident(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<Page<IncidentResponseDTO>> getAllIncidents(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<IncidentResponseDTO> incidents = incidentServiceV2.getAllIncidents(pageable);
        return ResponseEntity.ok(incidents);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(@PathVariable Long id) {
        logger.info("Getting incident by ID: {}", id);
        IncidentResponseDTO result = incidentServiceV2.getIncidentById(id);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> updateIncident(
            @PathVariable Long id, 
            @Valid @RequestBody IncidentUpdateDTO dto) {
        
        logger.info("Updating incident ID: {} with data: title={}, description={}, affectedService={}, reportedBy={}", 
                   id, dto.getTitle(), dto.getDescription(), dto.getAffectedService(), dto.getReportedBy());
        
        IncidentResponseDTO result = incidentServiceV2.updateIncident(id, dto);
        
        logger.info("Update completed. Result title: {}, description: {}", 
                   result.getTitle(), result.getDescription());
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{id}/updates")
    public ResponseEntity<IncidentResponseDTO> addUpdate(
            @PathVariable Long id,
            @Valid @RequestBody TimelineUpdateDTO dto) {
        
        logger.info("Adding update to incident ID: {}", id);
        IncidentResponseDTO result = incidentServiceV2.addUpdate(id, dto);
        return ResponseEntity.ok(result);
    }
}
