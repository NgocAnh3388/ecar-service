package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
}