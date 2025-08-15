package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentResponseDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.dto.TimelineResponseDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;
    
    @PostMapping
    public ResponseEntity<IncidentResponseDTO> createIncident(@Valid @RequestBody IncidentCreateDTO dto) {
        return new ResponseEntity<>(incidentService.createIncident(dto), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<Page<IncidentResponseDTO>> getAllIncidents(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Incident.IncidentStatus status,
            @RequestParam(required = false) Incident.SeverityLevel severityLevel,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String search) {
        
        Page<IncidentResponseDTO> incidents;
        
        if (status != null) {
            incidents = incidentService.getIncidentsByStatus(status, pageable);
        } else if (severityLevel != null) {
            incidents = incidentService.getIncidentsBySeverity(severityLevel, pageable);
        } else if (assignee != null) {
            incidents = incidentService.getIncidentsByAssignee(assignee, pageable);
        } else if (resolved != null) {
            incidents = incidentService.getIncidentsByResolved(resolved, pageable);
        } else if (service != null) {
            incidents = incidentService.getIncidentsByService(service, pageable);
        } else if (search != null) {
            incidents = incidentService.searchIncidents(search, pageable);
        } else {
            incidents = incidentService.getAllIncidents(pageable);
        }
        
        return ResponseEntity.ok(incidents);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponseDTO> updateIncident(
            @PathVariable Long id, 
            @Valid @RequestBody IncidentUpdateDTO dto) {
        
        try {
            System.out.println("CONTROLLER - Received DTO: title=" + dto.getTitle() + 
                             ", description=" + dto.getDescription() + 
                             ", affectedService=" + dto.getAffectedService() + 
                             ", reportedBy=" + dto.getReportedBy());
            
            IncidentResponseDTO result = incidentService.updateIncident(id, dto);
            System.out.println("UPDATE SUCCESS - Title in response: " + result.getTitle());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("UPDATE ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @PostMapping("/{id}/updates")
    public ResponseEntity<IncidentResponseDTO> addUpdate(
            @PathVariable Long id,
            @Valid @RequestBody TimelineUpdateDTO dto) {
        return ResponseEntity.ok(incidentService.addUpdate(id, dto));
    }
    
    @GetMapping("/{id}/updates")
    public ResponseEntity<List<TimelineResponseDTO>> getIncidentTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentTimeline(id));
    }
    
    @PutMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponseDTO> resolveIncident(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.resolveIncident(id));
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getIncidentStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalIncidents", incidentService.countTotalIncidents());
        statistics.put("activeIncidents", incidentService.countActiveIncidents());
        statistics.put("resolvedIncidents", incidentService.countResolvedIncidents());
        statistics.put("bySeverity", incidentService.countBySeverity());
        statistics.put("byStatus", incidentService.countByStatus());
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getIncidentStats() {
        return getIncidentStatistics();
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<IncidentResponseDTO> createIncidentFromWebhook(@RequestBody Map<String, Object> webhookData) {
        IncidentCreateDTO incidentDTO = incidentService.convertWebhookToIncidentDTO(webhookData);
        IncidentResponseDTO createdIncident = incidentService.createIncident(incidentDTO);
        return new ResponseEntity<>(createdIncident, HttpStatus.CREATED);
    }
}
