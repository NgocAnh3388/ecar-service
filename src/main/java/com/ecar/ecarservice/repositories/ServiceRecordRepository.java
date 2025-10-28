package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByLicensePlateOrderByServiceDateDesc(String licensePlate);
}