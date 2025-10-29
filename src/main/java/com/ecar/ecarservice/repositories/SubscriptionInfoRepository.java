package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.SubscriptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {
    Optional<SubscriptionInfo> findFirstByOwnerId(@Param("ownerId") Long ownerId);
}
