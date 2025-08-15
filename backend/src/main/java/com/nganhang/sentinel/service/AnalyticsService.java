package com.nganhang.sentinel.service;

import com.nganhang.sentinel.dto.DashboardMetricsDTO;
import com.nganhang.sentinel.dto.IncidentTimeSeriesDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final IncidentRepository incidentRepository;
    
    public DashboardMetricsDTO getDashboardMetrics() {
        List<Incident> allIncidents = incidentRepository.findAll();
        
        // Basic counts
        Long totalIncidents = (long) allIncidents.size();
        Long openIncidents = allIncidents.stream()
                .filter(i -> i.getStatus() == Incident.IncidentStatus.INVESTIGATING)
                .count();
        Long inProgressIncidents = allIncidents.stream()
                .filter(i -> i.getStatus() == Incident.IncidentStatus.IDENTIFIED || 
                            i.getStatus() == Incident.IncidentStatus.MONITORING)
                .count();
        Long resolvedIncidents = allIncidents.stream()
                .filter(i -> i.getStatus() == Incident.IncidentStatus.RESOLVED)
                .count();
        
        // Average resolution time
        Double averageResolutionTimeHours = calculateAverageResolutionTime(allIncidents);
        
        // Group by severity
        Map<String, Long> incidentsBySeverity = allIncidents.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSeverityLevel().toString(),
                        Collectors.counting()
                ));
        
        // Group by service
        Map<String, Long> incidentsByService = allIncidents.stream()
                .filter(i -> i.getAffectedService() != null && !i.getAffectedService().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        Incident::getAffectedService,
                        Collectors.counting()
                ));
        
        // Group by assignee
        Map<String, Long> incidentsByAssignee = allIncidents.stream()
                .filter(i -> i.getAssignee() != null && !i.getAssignee().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        Incident::getAssignee,
                        Collectors.counting()
                ));
        
        // Last 7 days trend
        Map<String, Long> incidentsTrend = getLast7DaysTrend(allIncidents);
        
        // Incidents by hour (heatmap data)
        Map<Integer, Long> incidentsByHour = getIncidentsByHour(allIncidents);
        
        return DashboardMetricsDTO.builder()
                .totalIncidents(totalIncidents)
                .openIncidents(openIncidents)
                .inProgressIncidents(inProgressIncidents)
                .resolvedIncidents(resolvedIncidents)
                .averageResolutionTimeHours(averageResolutionTimeHours)
                .incidentsBySeverity(incidentsBySeverity)
                .incidentsByService(incidentsByService)
                .incidentsByAssignee(incidentsByAssignee)
                .incidentsTrend(incidentsTrend)
                .incidentsByHour(incidentsByHour)
                .build();
    }
    
    public List<IncidentTimeSeriesDTO> getTimeSeriesData(LocalDate startDate, LocalDate endDate) {
        List<Incident> incidents = incidentRepository.findByCreatedAtBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
        
        Map<LocalDate, List<Incident>> incidentsByDate = incidents.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCreatedAt().toLocalDate()
                ));
        
        List<IncidentTimeSeriesDTO> result = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            List<Incident> dayIncidents = incidentsByDate.getOrDefault(current, Collections.emptyList());
            
            IncidentTimeSeriesDTO dto = IncidentTimeSeriesDTO.builder()
                    .date(current)
                    .totalCount((long) dayIncidents.size())
                    .openCount(dayIncidents.stream()
                            .filter(i -> i.getStatus() == Incident.IncidentStatus.INVESTIGATING)
                            .count())
                    .resolvedCount(dayIncidents.stream()
                            .filter(i -> i.getStatus() == Incident.IncidentStatus.RESOLVED)
                            .count())
                    .criticalCount(dayIncidents.stream()
                            .filter(i -> i.getSeverityLevel() == Incident.SeverityLevel.CRITICAL)
                            .count())
                    .highCount(dayIncidents.stream()
                            .filter(i -> i.getSeverityLevel() == Incident.SeverityLevel.HIGH)
                            .count())
                    .mediumCount(dayIncidents.stream()
                            .filter(i -> i.getSeverityLevel() == Incident.SeverityLevel.MEDIUM)
                            .count())
                    .lowCount(dayIncidents.stream()
                            .filter(i -> i.getSeverityLevel() == Incident.SeverityLevel.LOW)
                            .count())
                    .build();
            
            result.add(dto);
            current = current.plusDays(1);
        }
        
        return result;
    }
    
    private Double calculateAverageResolutionTime(List<Incident> incidents) {
        List<Incident> resolvedIncidents = incidents.stream()
                .filter(i -> i.isResolved() && i.getResolvedAt() != null)
                .collect(Collectors.toList());
        
        if (resolvedIncidents.isEmpty()) {
            return 0.0;
        }
        
        double totalHours = resolvedIncidents.stream()
                .mapToDouble(i -> {
                    Duration duration = Duration.between(i.getCreatedAt(), i.getResolvedAt());
                    return duration.toMinutes() / 60.0;
                })
                .sum();
        
        return totalHours / resolvedIncidents.size();
    }
    
    private Map<String, Long> getLast7DaysTrend(List<Incident> allIncidents) {
        LocalDate today = LocalDate.now();
        Map<String, Long> trend = new LinkedHashMap<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = allIncidents.stream()
                    .filter(incident -> incident.getCreatedAt().toLocalDate().equals(date))
                    .count();
            trend.put(date.toString(), count);
        }
        
        return trend;
    }
    
    private Map<Integer, Long> getIncidentsByHour(List<Incident> allIncidents) {
        Map<Integer, Long> hourlyCount = new LinkedHashMap<>();
        
        // Initialize all hours
        for (int hour = 0; hour < 24; hour++) {
            hourlyCount.put(hour, 0L);
        }
        
        // Count incidents by hour
        allIncidents.forEach(incident -> {
            int hour = incident.getCreatedAt().getHour();
            hourlyCount.put(hour, hourlyCount.get(hour) + 1);
        });
        
        return hourlyCount;
    }
}
