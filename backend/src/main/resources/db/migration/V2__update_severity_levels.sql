CREATE OR REPLACE FUNCTION update_severity_levels()
RETURNS VOID AS $$
BEGIN
    -- Atualizar SEV1 para CRITICAL
    UPDATE incidents SET severity_level = 'CRITICAL' WHERE severity_level = 'SEV1';
    
    -- Atualizar SEV2 para HIGH
    UPDATE incidents SET severity_level = 'HIGH' WHERE severity_level = 'SEV2';
    
    -- Atualizar SEV3 para MEDIUM
    UPDATE incidents SET severity_level = 'MEDIUM' WHERE severity_level = 'SEV3';
    
    -- Atualizar SEV4 para LOW
    UPDATE incidents SET severity_level = 'LOW' WHERE severity_level = 'SEV4';
END;
$$ LANGUAGE plpgsql;

SELECT update_severity_levels();
