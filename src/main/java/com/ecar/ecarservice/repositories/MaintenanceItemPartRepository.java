package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.MaintenanceItemPart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceItemPartRepository extends JpaRepository<MaintenanceItemPart, Long> {
    List<MaintenanceItemPart> findByMaintenanceHistoryId(Long maintenanceHistoryId);
    void deleteByMaintenanceHistoryId(Long maintenanceHistoryId);
}
