package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {


    @EntityGraph(attributePaths= {
            "vehicle",
            "vehicle.carModel"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT mh " +
            "FROM MaintenanceHistory mh " +
            "WHERE mh.owner.id = :ownerId " +
            "AND mh.vehicle.licensePlate LIKE %:searchValue% " +
            "ORDER BY mh.createdAt DESC")
    Page<MaintenanceHistory> search(@Param("ownerId") Long ownerId,
                                    @Param("searchValue") String searchValue,
                                    Pageable pageable);

    @EntityGraph(attributePaths = {
            "vehicle",
            "vehicle.carModel",
            "owner",
            "center",
            "staff",
            "technician"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT mh " +
            "FROM MaintenanceHistory mh " +
            "ORDER BY mh.status, mh.submittedAt")
    List<MaintenanceHistory> findAllWithinToday();

}
