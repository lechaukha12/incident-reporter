# Incident Reporter - Architecture Documentation v1.3.0

## Current Architecture (Before v1.3.0)

### Backend Structure
```
backend/
├── src/main/java/com/nganhang/sentinel/
│   ├── SentinelApplication.java
│   ├── config/CorsConfig.java
│   ├── controller/
│   │   ├── DebugController.java
│   │   └── IncidentController.java
│   ├── dto/
│   │   ├── IncidentCreateDTO.java
│   │   ├── IncidentResponseDTO.java
│   │   ├── IncidentUpdateDTO.java
│   │   ├── TimelineResponseDTO.java
│   │   └── TimelineUpdateDTO.java
│   ├── model/
│   │   ├── Incident.java
│   │   └── IncidentUpdate.java
│   ├── repository/
│   │   ├── IncidentRepository.java
│   │   └── IncidentUpdateRepository.java
│   └── service/
│       └── IncidentService.java
└── src/main/resources/
    ├── application.properties
    └── db/migration/
        ├── V1__create_incidents_table.sql
        ├── V2__update_severity_levels.sql
        └── V3__add_notes_and_root_cause.sql
```

### Database Schema
```sql
-- Incidents table
CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'OPEN',
    severity_level VARCHAR(50) DEFAULT 'LOW',
    affected_service VARCHAR(255),
    reported_by VARCHAR(255),
    assignee VARCHAR(255),
    notes TEXT,
    root_cause TEXT,
    resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

-- Incident Updates table
CREATE TABLE incident_updates (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    update_type VARCHAR(50) DEFAULT 'UPDATE',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (incident_id) REFERENCES incidents(id)
);
```

### Current API Endpoints
- GET /api/incidents - List all incidents
- GET /api/incidents/{id} - Get incident by ID
- POST /api/incidents - Create new incident
- PUT /api/incidents/{id} - Update incident
- PUT /api/incidents/{id}/resolve - Resolve incident
- GET /api/incidents/{id}/updates - Get incident timeline
- POST /api/incidents/{id}/updates - Add timeline update

### Current Features
- Basic CRUD operations for incidents
- Timeline tracking for all changes
- Status management (OPEN, IN_PROGRESS, RESOLVED)
- Severity levels (LOW, MEDIUM, HIGH, CRITICAL)
- Bootstrap 5.3.2 frontend
- Docker deployment
- PostgreSQL database
- Issue terminology (replaced "sự cố" with "issue")

## Planned v1.3.0 Features
1. Dashboard & Analytics with charts
2. Advanced Search & Filters
3. Real-time WebSocket updates
4. User Authentication & Roles
5. Comments System
6. Issue Templates
7. Bulk Operations
8. API Documentation
9. Mobile Responsiveness
10. Performance optimizations
