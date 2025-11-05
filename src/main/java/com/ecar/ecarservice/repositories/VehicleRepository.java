package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwnerIdAndActiveTrue(Long ownerId);

    Optional<Vehicle> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);

}
