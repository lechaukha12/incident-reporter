package com.nganhang.sentinel.dto;

import com.nganhang.sentinel.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentUpdateDTO {

    private String title;
    
    private String description;
    
    private Incident.IncidentStatus status;
    
    private Incident.SeverityLevel severityLevel;
    
    private String affectedService;
    
    private String assignee;
    
    private String reportedBy;
    
    private boolean resolved;
    
    private String notes;
    
    private String rootCause;
}
