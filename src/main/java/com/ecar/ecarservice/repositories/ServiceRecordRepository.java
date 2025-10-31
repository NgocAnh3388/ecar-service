package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    // Tìm lịch sử bảo dưỡng của một xe, sắp xếp theo ngày gần nhất
    List<ServiceRecord> findByLicensePlateOrderByServiceDateDesc(String licensePlate);
}
