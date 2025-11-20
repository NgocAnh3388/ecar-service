package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Tính tổng chi phí trong một khoảng ngày (dùng LocalDate để khớp với Entity).
     */
    @Query("SELECT SUM(e.amount) FROM Expense e " +
            "WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}