package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Tìm bản ghi tồn kho của 1 phụ tùng tại 1 center cụ thể
    Optional<Inventory> findByCenterIdAndSparePartId(Long centerId, Long sparePartId);

    // Tìm tất cả tồn kho của một phụ tùng ở mọi center
    List<Inventory> findBySparePartId(Long sparePartId);

    // Tìm tất cả các phụ tùng sắp hết hàng tại một center cụ thể
    @Query("SELECT i FROM Inventory i WHERE i.center.id = :centerId AND i.stockQuantity < i.minStockLevel")
    List<Inventory> findLowStockPartsByCenter(Long centerId);

    List<Inventory> findByCenterId(Long centerId); // Thêm phương thức này

    void deleteBySparePartId(Long sparePartId); // Thêm phương thức này

}
