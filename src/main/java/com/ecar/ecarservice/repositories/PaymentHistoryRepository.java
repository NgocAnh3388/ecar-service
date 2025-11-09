package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    PaymentHistory findFirstByPaymentId(String paymentId);

    List<PaymentHistory> findAllBySubscriptionId(Long subscriptionId);

    /**
     * Tính tổng doanh thu từ các thanh toán đã APPROVED trong một khoảng thời gian.
     */
    @Query("SELECT SUM(ph.amount) FROM PaymentHistory ph " +
            "WHERE ph.paymentStatus = :status " +
            "AND ph.updatedAt BETWEEN :startDate AND :endDate") // <-- THAY ĐỔI Ở ĐÂY
    BigDecimal sumRevenueByStatusAndDateRange(
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}