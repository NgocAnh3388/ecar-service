package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.ServicePartUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServicePartUsageRepository extends JpaRepository<ServicePartUsage, Long> {
    // Lấy tất cả lịch sử sử dụng, sắp xếp theo ngày mới nhất
    @Query("SELECT spu FROM ServicePartUsage spu ORDER BY spu.serviceRecord.serviceDate DESC")
    List<ServicePartUsage> findAllOrderByServiceDateDesc();
}