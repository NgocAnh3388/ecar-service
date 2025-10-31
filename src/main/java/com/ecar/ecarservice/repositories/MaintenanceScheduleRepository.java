package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceSchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {
    @EntityGraph(attributePaths = {
            "carModel",
            "maintenanceMileStone",
            "service"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT ms  " +
            "FROM MaintenanceSchedule ms " +
            "WHERE ms.carModel.id = :carModelId " +
            "AND ms.maintenanceMileStone.id = :maintenanceMilestoneId")
    List<MaintenanceSchedule> findAllScheduleByCarModelIdAndMilestoneId(Long carModelId, Long maintenanceMilestoneId);
}
