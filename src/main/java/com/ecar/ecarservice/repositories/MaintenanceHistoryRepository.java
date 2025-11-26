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

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {

    // =================== TRUY VẤN CHO KHÁCH HÀNG ===================
    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel"})
    @Query("SELECT mh FROM MaintenanceHistory mh WHERE mh.owner.id = :ownerId AND mh.vehicle.licensePlate LIKE CONCAT('%', :searchValue, '%') ORDER BY mh.createdAt DESC")
    Page<MaintenanceHistory> searchByOwner(@Param("ownerId") Long ownerId, @Param("searchValue") String searchValue, Pageable pageable);

    // =================== TRUY VẤN CHO QUẢN LÝ (ADMIN/STAFF) ===================
    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel", "owner", "center", "staff", "technician"})
    @Query("SELECT mh FROM MaintenanceHistory mh ORDER BY mh.status ASC, mh.submittedAt DESC")
    List<MaintenanceHistory> findAllAndSortForManagement();

    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel", "owner", "center", "staff", "technician"})
    @Query("SELECT mh FROM MaintenanceHistory mh WHERE mh.center.id = :centerId ORDER BY mh.status ASC, mh.submittedAt DESC")
    List<MaintenanceHistory> findAllByCenterIdSortedForManagement(@Param("centerId") Long centerId);

    // =================== TRUY VẤN CHO KỸ THUẬT VIÊN (TECHNICIAN) ===================
    @EntityGraph(attributePaths = {"vehicle", "vehicle.carModel", "owner", "center", "staff", "technician"})
    List<MaintenanceHistory> findByTechnicianIdOrderByStatusAscTechnicianReceivedAtDesc(Long technicianId);

    // --- DTO Projection để lấy số lượng công việc của Technician ---
    interface TechnicianLoad {
        Long getTechnicianId();
        Long getLoadCount();
    }

    /**
     * Đếm số lượng phiếu đang ở một trạng thái cụ thể (e.g., TECHNICIAN_RECEIVED)
     * cho một danh sách các technician. Dùng cho nghiệp vụ quản lý quá tải.
     */
    @Query("SELECT mh.technician.id as technicianId, COUNT(mh.id) as loadCount " +
            "FROM MaintenanceHistory mh " +
            "WHERE mh.status = :status AND mh.technician.id IN :technicianIds " +
            "GROUP BY mh.technician.id")
    List<TechnicianLoad> getTechnicianLoadByStatusAndIds(@Param("status") MaintenanceStatus status, @Param("technicianIds") List<Long> technicianIds);


    // --- Đếm số lượng task của Technician trong 1 ngày ---
    @Query("SELECT COUNT(m) FROM MaintenanceHistory m " +
            "WHERE m.technician.id = :technicianId " +
            "AND m.scheduleDate = :date " +
            "AND m.status <> 'CANCELLED'")
    long countTasksByTechnicianAndDate(@Param("technicianId") Long technicianId,
                                       @Param("date") LocalDate date);
}