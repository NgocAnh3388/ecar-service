package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.ServiceRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRecordDetailRepository extends JpaRepository<ServiceRecordDetail, Long> {
}