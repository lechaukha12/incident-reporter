package com.nganhang.sentinel.repository;

import com.nganhang.sentinel.model.Incident;
import com.nganhang.sentinel.model.IncidentUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentUpdateRepository extends JpaRepository<IncidentUpdate, Long> {

    List<IncidentUpdate> findByIncidentOrderByCreatedAtDesc(Incident incident);
    
    List<IncidentUpdate> findByIncidentIdOrderByCreatedAtDesc(Long incidentId);
    
    List<IncidentUpdate> findByUpdateType(IncidentUpdate.UpdateType updateType);
}
