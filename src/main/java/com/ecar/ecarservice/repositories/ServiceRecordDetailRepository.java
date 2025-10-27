package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.ServiceRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRecordDetailRepository extends JpaRepository<ServiceRecordDetail, Long> {
}