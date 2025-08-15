-- V5__create_admin_user.sql
INSERT INTO users (username, password, full_name, email, role, status, created_at) 
VALUES 
('admin', 'admin123', 'Administrator', 'admin@tuningops.com', 'ADMIN', 'ACTIVE', NOW()),
('user1', 'user123', 'Nguyễn Văn A', 'user1@tuningops.com', 'USER', 'ACTIVE', NOW()),
('manager1', 'manager123', 'Trần Thị B', 'manager1@tuningops.com', 'MANAGER', 'ACTIVE', NOW())
ON CONFLICT (username) DO NOTHING;
