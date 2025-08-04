package com.nganhang.sentinel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_updates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IncidentUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "update_type")
    @Enumerated(EnumType.STRING)
    private UpdateType updateType;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum UpdateType {
        STATUS_CHANGE,
        INVESTIGATION,
        ROOT_CAUSE,
        ACTION_TAKEN,
        RESOLUTION,
        OTHER
    }
}
