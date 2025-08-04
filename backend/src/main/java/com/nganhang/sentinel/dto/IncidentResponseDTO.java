package com.nganhang.sentinel.dto;

import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.model.IncidentUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Incident.IncidentStatus status;
    private Incident.SeverityLevel severityLevel;
    private String affectedService;
    private String assignee;
    private String reportedBy;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    private String rootCause;
    private List<IncidentUpdateResponseDTO> timeline;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncidentUpdateResponseDTO {
        private Long id;
        private String message;
        private IncidentUpdate.UpdateType updateType;
        private String createdBy;
        private LocalDateTime createdAt;
    }
}
