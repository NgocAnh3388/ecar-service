package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    Optional<CarModel> findByNameIgnoreCase(String name);
}