package com.nganhang.sentinel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severityLevel;

    @Column(name = "affected_service")
    private String affectedService;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "reported_by", nullable = false)
    private String reportedBy;

    @Column(name = "is_resolved")
    private boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentUpdate> updates = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum IncidentStatus {
        INVESTIGATING,
        IDENTIFIED,
        MONITORING,
        RESOLVED
    }

    public enum SeverityLevel {
        SEV1, // Critical - Urgent service outage affecting many users
        SEV2, // High - Significant functionality issue affecting some users
        SEV3, // Medium - Minor functionality issue affecting few users
        SEV4  // Low - Minimal impact, typically for improvements or non-urgent fixes
    }
}
