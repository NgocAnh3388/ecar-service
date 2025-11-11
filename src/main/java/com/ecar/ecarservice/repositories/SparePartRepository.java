package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    List<SparePart> findByCarModel_Id(Long carModelId);

//    @Query("SELECT p FROM SparePart p WHERE p.stockQuantity < p.minStockLevel")
//    List<SparePart> findLowStockParts();
}
