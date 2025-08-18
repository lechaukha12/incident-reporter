-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    manager_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT true
);

-- Add department_id to incidents table if not exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='incidents' AND column_name='department_id') THEN
        ALTER TABLE incidents ADD COLUMN department_id BIGINT REFERENCES departments(id);
    END IF;
END $$;

-- Insert default departments
INSERT INTO departments (name, description, is_active, created_at) VALUES
('IT Operations', 'Phòng Vận hành Hệ thống IT', true, CURRENT_TIMESTAMP),
('Development', 'Phòng Phát triển Phần mềm', true, CURRENT_TIMESTAMP),
('Customer Support', 'Phòng Hỗ trợ Khách hàng', true, CURRENT_TIMESTAMP),
('Network Security', 'Phòng An ninh Mạng', true, CURRENT_TIMESTAMP),
('Infrastructure', 'Phòng Hạ tầng IT', true, CURRENT_TIMESTAMP),
('Quality Assurance', 'Phòng Kiểm thử Chất lượng', true, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
