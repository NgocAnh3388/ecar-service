package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.ServiceSparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceSparePartRepository extends JpaRepository<ServiceSparePart, Long> {
    // Tìm tất cả các phụ tùng cần thiết cho một danh sách các dịch vụ
    @Query("SELECT DISTINCT ssp FROM ServiceSparePart ssp " +
            "JOIN FETCH ssp.sparePart sp " +
            "JOIN FETCH sp.carModel " +
            "WHERE ssp.service.id IN :serviceIds AND sp.carModel.id = :carModelId")
    List<ServiceSparePart> findByServiceIdsAndCarModelId(List<Long> serviceIds, Long carModelId);
}
