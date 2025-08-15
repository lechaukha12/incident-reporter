package com.nganhang.sentinel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentTimeSeriesDTO {
    private LocalDate date;
    private Long totalCount;
    private Long openCount;
    private Long resolvedCount;
    private Long criticalCount;
    private Long highCount;
    private Long mediumCount;
    private Long lowCount;
}
