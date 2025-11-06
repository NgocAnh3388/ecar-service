package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.enums.PaymentStatus;
import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import com.ecar.ecarservice.repositories.ExpenseRepository;
import com.ecar.ecarservice.repositories.PaymentHistoryRepository;
import com.ecar.ecarservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ExpenseRepository expenseRepository; // <-- Inject Repo Chi phí

    @Override
    public ProfitReportResponse getProfitReport(LocalDate startDate, LocalDate endDate) {

        // 1. TÍNH TỔNG DOANH THU (Revenue)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = paymentHistoryRepository.sumRevenueByStatusAndDateRange(
                PaymentStatus.APPROVED.name(), // Chỉ tính giao dịch THÀNH CÔNG
                startDateTime,
                endDateTime
        );
        totalRevenue = (totalRevenue == null) ? BigDecimal.ZERO : totalRevenue;

        // 2. TÍNH TỔNG CHI PHÍ (Expense)
        BigDecimal totalExpense = expenseRepository.sumExpenseByDateRange(
                startDate,
                endDate
        );
        totalExpense = (totalExpense == null) ? BigDecimal.ZERO : totalExpense;

        // 3. TÍNH LỢI NHUẬN (Profit)
        BigDecimal netProfit = totalRevenue.subtract(totalExpense);

        // 4. Trả về DTO
        return ProfitReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .totalExpense(totalExpense)
                .netProfit(netProfit)
                .currency("USD") // Hoặc lấy từ config
                .build();
    }
}