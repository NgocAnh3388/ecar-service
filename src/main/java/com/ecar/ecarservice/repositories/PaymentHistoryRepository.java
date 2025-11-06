package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    PaymentHistory findFirstByPaymentId(String paymentId);

    List<PaymentHistory> findAllBySubscriptionId(Long subscriptionId);
}
