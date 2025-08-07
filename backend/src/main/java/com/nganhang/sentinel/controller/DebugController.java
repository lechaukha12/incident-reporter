package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "http://localhost:4200")
public class DebugController {

    @Autowired
    private IncidentRepository incidentRepository;

    @GetMapping("/incident/{id}")
    public Map<String, Object> getIncidentDebugInfo(@PathVariable Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow();
        Map<String, Object> debug = new HashMap<>();
        debug.put("id", incident.getId());
        debug.put("title", incident.getTitle());
        debug.put("description", incident.getDescription());
        debug.put("affectedService", incident.getAffectedService());
        debug.put("reportedBy", incident.getReportedBy());
        debug.put("assignee", incident.getAssignee());
        debug.put("notes", incident.getNotes());
        debug.put("rootCause", incident.getRootCause());
        debug.put("severityLevel", incident.getSeverityLevel());
        debug.put("status", incident.getStatus());
        debug.put("entityToString", incident.toString());
        return debug;
    }
}
