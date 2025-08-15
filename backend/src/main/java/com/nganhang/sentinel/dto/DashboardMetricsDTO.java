package com.nganhang.sentinel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {
    private Long totalIncidents;
    private Long openIncidents;
    private Long inProgressIncidents;
    private Long resolvedIncidents;
    private Double averageResolutionTimeHours;
    private Map<String, Long> incidentsBySeverity;
    private Map<String, Long> incidentsByService;
    private Map<String, Long> incidentsByAssignee;
    private Map<String, Long> incidentsTrend; // last 7 days
    private Map<Integer, Long> incidentsByHour; // 0-23 hours
}
