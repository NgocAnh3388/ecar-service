package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceSchedule;
import com.ecar.ecarservice.enums.MaintenanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {
    List<MaintenanceSchedule> findByCarModelIdAndKilometerMark(Long carModelId, int kilometerMark);

    @Query("SELECT MAX(ms.kilometerMark) FROM MaintenanceSchedule ms WHERE ms.carModel.id = :carModelId AND ms.kilometerMark <= :kilometers")
    Optional<Integer> findClosestKilometerMark(
            @Param("carModelId") Long carModelId,
            @Param("kilometers") int kilometers);

    @Query("SELECT ms FROM MaintenanceSchedule ms " +
            "JOIN FETCH ms.item mi " +
            "JOIN FETCH ms.carModel cm " +
            "WHERE lower(cm.name) = lower(:carModelName) " +
            "AND lower(mi.name) = lower(:itemName) " +
            "AND ms.action = :action " +
            "AND ms.kilometerMark = :kilometerMark")
    Optional<MaintenanceSchedule> findScheduleByDetails(
            @Param("carModelName") String carModelName,
            @Param("itemName") String itemName,
            @Param("action") MaintenanceAction action,
            @Param("kilometerMark") int kilometerMark);
}