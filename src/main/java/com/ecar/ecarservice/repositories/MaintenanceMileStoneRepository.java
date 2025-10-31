package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceMileStone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceMileStoneRepository extends JpaRepository<MaintenanceMileStone, Long> {
    List<MaintenanceMileStone> findALlByCarModelIdOrderByYearAt(Long carModelId);

}
