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

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "is_resolved")
    private boolean resolved;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

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
        CRITICAL, // Nghiêm trọng - Issue ảnh hưởng đến nhiều người dùng
        HIGH,     // Cao - Vấn đề chức năng quan trọng ảnh hưởng đến một số người dùng
        MEDIUM,   // Trung bình - Vấn đề chức năng nhỏ ảnh hưởng đến ít người dùng
        LOW       // Thấp - Tác động tối thiểu, thường cho cải tiến hoặc sửa lỗi không khẩn cấp
    }
}
