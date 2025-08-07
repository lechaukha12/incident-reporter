package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentResponseDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.service.IncidentServiceV3;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/incidents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IncidentControllerV3 {

    private static final Logger logger = LoggerFactory.getLogger(IncidentControllerV3.class);
    
    private final IncidentServiceV3 incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponseDTO> createIncident(@RequestBody IncidentCreateDTO incidentDTO) {
        logger.info("V3 Creating incident with title: {}", incidentDTO.getTitle());
        IncidentResponseDTO response = incidentService.createIncident(incidentDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<IncidentResponseDTO>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<IncidentResponseDTO> incidents = incidentService.getAllIncidents(pageable);
        
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(@PathVariable Long id) {
        logger.info("V3 Getting incident by ID: {}", id);
        IncidentResponseDTO incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> updateIncident(
            @PathVariable Long id, 
            @RequestBody IncidentUpdateDTO incidentDTO) {
        
        logger.info("=== V3 UPDATE REQUEST === ID: {}", id);
        logger.info("V3 Request body - Title: '{}', Description: '{}'", 
                   incidentDTO.getTitle(), incidentDTO.getDescription());
        logger.info("V3 Request body - Service: '{}', Reporter: '{}'", 
                   incidentDTO.getAffectedService(), incidentDTO.getReportedBy());
        
        IncidentResponseDTO response = incidentService.updateIncident(id, incidentDTO);
        
        logger.info("V3 Response - Title: '{}', Description: '{}'", 
                   response.getTitle(), response.getDescription());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/updates")
    public ResponseEntity<IncidentResponseDTO> addUpdate(
            @PathVariable Long id, 
            @RequestBody TimelineUpdateDTO updateDTO) {
        
        logger.info("V3 Adding update to incident ID: {}", id);
        IncidentResponseDTO response = incidentService.addUpdate(id, updateDTO);
        return ResponseEntity.ok(response);
    }
}
