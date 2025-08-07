package com.nganhang.sentinel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false, length = 20)
    private SeverityLevel severityLevel;

    @Column(name = "affected_service", length = 100)
    private String affectedService;

    @Column(name = "assignee", length = 100)
    private String assignee;

    @Column(name = "reported_by", nullable = false, length = 100)
    private String reportedBy;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved = false;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentUpdate> updates = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Manual timestamp management
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum IncidentStatus {
        INVESTIGATING,
        IDENTIFIED,
        MONITORING,
        RESOLVED
    }

    public enum SeverityLevel {
        CRITICAL, // Nghiêm trọng - Sự cố ảnh hưởng đến nhiều người dùng
        HIGH,     // Cao - Vấn đề chức năng quan trọng ảnh hưởng đến một số người dùng
        MEDIUM,   // Trung bình - Vấn đề chức năng nhỏ ảnh hưởng đến ít người dùng
        LOW       // Thấp - Tác động tối thiểu, thường cho cải tiến hoặc sửa lỗi không khẩn cấp
    }
}
