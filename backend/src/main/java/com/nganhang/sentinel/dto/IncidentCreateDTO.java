package com.nganhang.sentinel.dto;

import com.nganhang.sentinel.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCreateDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    
    private String description;
    
    @NotNull(message = "Mức độ nghiêm trọng không được để trống")
    private Incident.SeverityLevel severityLevel;
    
    private String affectedService;
    
    private String assignee;
    
    @NotBlank(message = "Người báo cáo không được để trống")
    private String reportedBy;
}
