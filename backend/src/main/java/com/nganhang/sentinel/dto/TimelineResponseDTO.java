package com.nganhang.sentinel.dto;

import com.nganhang.sentinel.model.IncidentUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineResponseDTO {
    
    private Long id;
    private String message;
    private IncidentUpdate.UpdateType updateType;
    private String createdBy;
    private LocalDateTime timestamp;
}
