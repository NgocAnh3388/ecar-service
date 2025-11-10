package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {
    @EntityGraph(attributePaths = {
            "vehicle",
            "vehicle.carModel"
    }, type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT mh " +
            "FROM MaintenanceHistory mh " +
            "WHERE mh.owner.id = :ownerId " +
            "AND mh.vehicle.licensePlate LIKE %:searchValue% " +
            "ORDER BY mh.createdAt DESC")
    Page<MaintenanceHistory> searchByOwner(@Param("ownerId") Long ownerId,
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
            "ORDER BY mh.status ASC, mh.submittedAt DESC") // Sắp xếp theo trạng thái và ngày gửi mới nhất
    List<MaintenanceHistory> findAllAndSortForManagement();


    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel", "owner", "center", "staff", "technician"})
    List<MaintenanceHistory> findByTechnicianIdOrderByStatusAscTechnicianReceivedAtDesc(Long technicianId);

    Optional<MaintenanceHistory> findFirstByVehicleLicensePlateAndStatusNotIn(String licensePlate, List<MaintenanceStatus> statuses);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel", "owner", "center", "staff", "technician"})
    List<MaintenanceHistory> findByTechnicianIdOrderByStatusAscSubmittedAtAsc(Long technicianId);

}