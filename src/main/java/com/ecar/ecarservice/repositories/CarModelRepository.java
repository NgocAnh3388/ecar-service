package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
}
