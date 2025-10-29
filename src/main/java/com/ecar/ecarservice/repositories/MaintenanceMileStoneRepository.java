package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceMileStone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceMileStoneRepository extends JpaRepository<MaintenanceMileStone, Long> {
}