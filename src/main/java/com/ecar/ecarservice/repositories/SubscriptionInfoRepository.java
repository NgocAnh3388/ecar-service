package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.SubscriptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {
    Optional<SubscriptionInfo> findFirstByOwnerId(@Param("ownerId") Long ownerId);

    SubscriptionInfo findFirstById(Long id);

    List<SubscriptionInfo> findByOwnerId(Long ownerId);
}
