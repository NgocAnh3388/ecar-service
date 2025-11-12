package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
//    List<Vehicle> findByOwnerIdAndActiveTrue(Long ownerId);
    List<Vehicle> findByOwnerIdAndActiveTrueOrderByIdAsc(Long ownerId);

    Optional<Vehicle> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);

    // Trong VehicleRepository.java
    @Query("SELECT v FROM Vehicle v WHERE v.nextDate IS NOT NULL AND CAST(v.nextDate AS date) = :targetDate")
    List<Vehicle> findVehiclesDueForMaintenanceOn(@Param("targetDate") LocalDate targetDate);

}
