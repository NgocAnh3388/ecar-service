package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {

    @Query(value = "SELECT mh " +
            "FROM MaintenanceHistory mh " +
            "WHERE mh.owner_id = :ownerId " +
            "AND mh.vehicle.licensePlate LIKE %:searchValue% " +
            "ORDER BY mh.createdAt DESC")
    Page<MaintenanceHistory> search(@Param("ownerId") Long ownerId,
                                    @Param("searchValue") String searchValue,
                                    Pageable pageable);
}