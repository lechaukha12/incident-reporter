package com.nganhang.sentinel.service;

import com.nganhang.sentinel.dto.IncidentCreateDTO;
import com.nganhang.sentinel.dto.IncidentResponseDTO;
import com.nganhang.sentinel.dto.IncidentUpdateDTO;
import com.nganhang.sentinel.dto.TimelineUpdateDTO;
import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.model.IncidentUpdate;
import com.nganhang.sentinel.repository.IncidentRepository;
import com.nganhang.sentinel.repository.IncidentUpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentUpdateRepository incidentUpdateRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Incident testIncident;
    private IncidentUpdate testUpdate;
    private IncidentCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testIncident = Incident.builder()
                .id(1L)
                .title("Test Incident")
                .description("Test Description")
                .status(Incident.IncidentStatus.INVESTIGATING)
                .severityLevel(Incident.SeverityLevel.SEV1)
                .affectedService("Test Service")
                .reportedBy("Test User")
                .resolved(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUpdate = IncidentUpdate.builder()
                .id(1L)
                .incident(testIncident)
                .message("Initial update")
                .updateType(IncidentUpdate.UpdateType.STATUS_CHANGE)
                .createdBy("Test User")
                .createdAt(LocalDateTime.now())
                .build();

        createDTO = IncidentCreateDTO.builder()
                .title("Test Incident")
                .description("Test Description")
                .severityLevel(Incident.SeverityLevel.SEV1)
                .affectedService("Test Service")
                .reportedBy("Test User")
                .build();
    }

    @Test
    void createIncident_ShouldReturnCreatedIncident() {
        // Arrange
        when(incidentRepository.save(any(Incident.class))).thenReturn(testIncident);
        when(incidentUpdateRepository.save(any(IncidentUpdate.class))).thenReturn(testUpdate);
        when(incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(any(Incident.class)))
                .thenReturn(Collections.singletonList(testUpdate));

        // Act
        IncidentResponseDTO result = incidentService.createIncident(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testIncident.getId(), result.getId());
        assertEquals(testIncident.getTitle(), result.getTitle());
        assertEquals(Incident.IncidentStatus.INVESTIGATING, result.getStatus());
        assertEquals(1, result.getTimeline().size());

        verify(incidentRepository).save(any(Incident.class));
        verify(incidentUpdateRepository).save(any(IncidentUpdate.class));
    }

    @Test
    void getIncidentById_WhenIncidentExists_ShouldReturnIncident() {
        // Arrange
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(testIncident))
                .thenReturn(Collections.singletonList(testUpdate));

        // Act
        IncidentResponseDTO result = incidentService.getIncidentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testIncident.getId(), result.getId());
        assertEquals(testIncident.getTitle(), result.getTitle());
        assertEquals(1, result.getTimeline().size());

        verify(incidentRepository).findById(1L);
        verify(incidentUpdateRepository).findByIncidentOrderByCreatedAtDesc(testIncident);
    }

    @Test
    void getIncidentById_WhenIncidentDoesNotExist_ShouldThrowException() {
        // Arrange
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> incidentService.getIncidentById(999L));
        verify(incidentRepository).findById(999L);
    }

    @Test
    void updateIncident_WhenIncidentExists_ShouldReturnUpdatedIncident() {
        // Arrange
        IncidentUpdateDTO updateDTO = new IncidentUpdateDTO();
        updateDTO.setStatus(Incident.IncidentStatus.IDENTIFIED);
        updateDTO.setSeverityLevel(Incident.SeverityLevel.SEV2);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(testIncident);
        when(incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(testIncident))
                .thenReturn(Collections.singletonList(testUpdate));

        // Act
        IncidentResponseDTO result = incidentService.updateIncident(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(Incident.IncidentStatus.IDENTIFIED, result.getStatus());
        assertEquals(Incident.SeverityLevel.SEV2, result.getSeverityLevel());

        verify(incidentRepository).findById(1L);
        verify(incidentRepository).save(testIncident);
    }

    @Test
    void addUpdate_WhenIncidentExists_ShouldAddUpdateAndReturnIncident() {
        // Arrange
        TimelineUpdateDTO updateDTO = new TimelineUpdateDTO();
        updateDTO.setMessage("New update");
        updateDTO.setUpdateType(IncidentUpdate.UpdateType.INVESTIGATION);
        updateDTO.setCreatedBy("Another User");

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentUpdateRepository.save(any(IncidentUpdate.class))).thenReturn(testUpdate);
        when(incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(testIncident))
                .thenReturn(Collections.singletonList(testUpdate));

        // Act
        IncidentResponseDTO result = incidentService.addUpdate(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testIncident.getId(), result.getId());
        assertEquals(1, result.getTimeline().size());

        verify(incidentRepository).findById(1L);
        verify(incidentUpdateRepository).save(any(IncidentUpdate.class));
        verify(incidentUpdateRepository).findByIncidentOrderByCreatedAtDesc(testIncident);
    }

    @Test
    void getAllIncidents_ShouldReturnPageOfIncidents() {
        // Arrange
        Page<Incident> incidentPage = new PageImpl<>(Collections.singletonList(testIncident));

        when(incidentRepository.findAll(any(Pageable.class))).thenReturn(incidentPage);
        when(incidentUpdateRepository.findByIncidentOrderByCreatedAtDesc(testIncident))
                .thenReturn(Collections.singletonList(testUpdate));

        // Act
        Page<IncidentResponseDTO> result = incidentService.getAllIncidents(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testIncident.getId(), result.getContent().get(0).getId());

        verify(incidentRepository).findAll(any(Pageable.class));
    }

    @Test
    void countStatistics_ShouldReturnCorrectCounts() {
        // Arrange
        when(incidentRepository.count()).thenReturn(10L);
        when(incidentRepository.findByResolvedFalse(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testIncident)));
        when(incidentRepository.findByResolvedTrue(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        for (Incident.SeverityLevel level : Incident.SeverityLevel.values()) {
            when(incidentRepository.findBySeverityLevel(eq(level), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(level == Incident.SeverityLevel.SEV1 ? 
                            List.of(testIncident) : Collections.emptyList()));
        }

        for (Incident.IncidentStatus status : Incident.IncidentStatus.values()) {
            when(incidentRepository.findByStatus(eq(status), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(status == Incident.IncidentStatus.INVESTIGATING ? 
                            List.of(testIncident) : Collections.emptyList()));
        }

        // Act
        long totalCount = incidentService.countTotalIncidents();
        long activeCount = incidentService.countActiveIncidents();
        long resolvedCount = incidentService.countResolvedIncidents();
        Map<Incident.SeverityLevel, Long> severityCounts = incidentService.countBySeverity();
        Map<Incident.IncidentStatus, Long> statusCounts = incidentService.countByStatus();

        // Assert
        assertEquals(10L, totalCount);
        assertEquals(1L, activeCount);
        assertEquals(0L, resolvedCount);
        
        assertEquals(1L, severityCounts.get(Incident.SeverityLevel.SEV1));
        assertEquals(0L, severityCounts.get(Incident.SeverityLevel.SEV2));
        
        assertEquals(1L, statusCounts.get(Incident.IncidentStatus.INVESTIGATING));
        assertEquals(0L, statusCounts.get(Incident.IncidentStatus.RESOLVED));
    }

    @Test
    void convertWebhookToIncidentDTO_ShouldReturnCorrectDTO() {
        // Arrange
        Map<String, Object> webhookData = Map.of(
            "title", "Test Alert",
            "description", "Test Description",
            "severity", "critical",
            "service", "Test Service",
            "reportedBy", "Monitoring System"
        );

        // Act
        IncidentCreateDTO result = incidentService.convertWebhookToIncidentDTO(webhookData);

        // Assert
        assertNotNull(result);
        assertEquals("Test Alert", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(Incident.SeverityLevel.SEV1, result.getSeverityLevel());
        assertEquals("Test Service", result.getAffectedService());
        assertEquals("Monitoring System", result.getReportedBy());
    }
}
