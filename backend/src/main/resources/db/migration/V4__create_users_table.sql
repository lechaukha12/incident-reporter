-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    department VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_department ON users(department);

-- Insert sample users
INSERT INTO users (username, password, full_name, email, phone_number, department, role, status) VALUES
('admin', 'admin123', 'Quản trị viên hệ thống', 'admin@namabank.com', '0901234567', 'IT', 'ADMIN', 'ACTIVE'),
('manager1', 'manager123', 'Nguyễn Văn Quản lý', 'manager1@namabank.com', '0901234568', 'Operations', 'MANAGER', 'ACTIVE'),
('user1', 'user123', 'Trần Thị Nhân viên', 'user1@namabank.com', '0901234569', 'Customer Service', 'USER', 'ACTIVE'),
('user2', 'user123', 'Lê Văn Kỹ thuật', 'user2@namabank.com', '0901234570', 'IT', 'USER', 'ACTIVE'),
('user3', 'user123', 'Phạm Thị Kiểm soát', 'user3@namabank.com', '0901234571', 'Risk Management', 'USER', 'ACTIVE');
