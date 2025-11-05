package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, Long> {

    @Query("SELECT mi.serviceId FROM MaintenanceItem mi WHERE mi.maintenanceHistoryId = :ticketId")
    List<Long> findAllServiceIds(@Param("ticketId") Long ticketId);
}
