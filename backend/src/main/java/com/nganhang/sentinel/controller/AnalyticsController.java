package com.nganhang.sentinel.controller;

import com.nganhang.sentinel.dto.DashboardMetricsDTO;
import com.nganhang.sentinel.dto.IncidentTimeSeriesDTO;
import com.nganhang.sentinel.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        log.info("Getting dashboard metrics");
        try {
            DashboardMetricsDTO metrics = analyticsService.getDashboardMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting dashboard metrics", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/time-series")
    public ResponseEntity<List<IncidentTimeSeriesDTO>> getTimeSeriesData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting time series data from {} to {}", startDate, endDate);
        
        try {
            List<IncidentTimeSeriesDTO> data = analyticsService.getTimeSeriesData(startDate, endDate);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error getting time series data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
