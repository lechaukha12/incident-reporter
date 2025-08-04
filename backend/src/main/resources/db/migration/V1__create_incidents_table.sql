CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    severity_level VARCHAR(10) NOT NULL,
    affected_service VARCHAR(100),
    assignee VARCHAR(100),
    reported_by VARCHAR(100) NOT NULL,
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE incident_updates (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    update_type VARCHAR(20),
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_incident_status ON incidents(status);
CREATE INDEX idx_incident_severity ON incidents(severity_level);
CREATE INDEX idx_incident_assignee ON incidents(assignee);
CREATE INDEX idx_incident_resolved ON incidents(is_resolved);
CREATE INDEX idx_incident_created_at ON incidents(created_at);

CREATE INDEX idx_update_incident_id ON incident_updates(incident_id);
CREATE INDEX idx_update_created_at ON incident_updates(created_at);
