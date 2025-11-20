package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.payload.responses.MonthlyDetail;
import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import com.ecar.ecarservice.repositories.ExpenseRepository;
import com.ecar.ecarservice.repositories.PaymentHistoryRepository;
import com.ecar.ecarservice.service.ReportService;
import com.ecar.ecarservice.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public ProfitReportResponse getProfitReport(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        // 1. TÍNH TỔNG TOÀN BỘ (Header Cards)
        BigDecimal totalRevenue = paymentHistoryRepository.sumRevenueByStatusAndDateRange(
                PaymentStatus.APPROVED.name(), startDateTime, endDateTime
        );
        totalRevenue = Optional.ofNullable(totalRevenue).orElse(BigDecimal.ZERO);

        // Lưu ý: expenseRepository dùng LocalDate
        BigDecimal totalExpense = expenseRepository.sumExpenseByDateRange(
                startDateTime.toLocalDate(), endDateTime.toLocalDate()
        );
        totalExpense = Optional.ofNullable(totalExpense).orElse(BigDecimal.ZERO);


        // 2. TÍNH CHI TIẾT TỪNG THÁNG (Cho Biểu đồ)
        List<MonthlyDetail> details = new ArrayList<>();

        YearMonth current = YearMonth.from(startDateTime);
        YearMonth end = YearMonth.from(endDateTime);

        // Vòng lặp qua từng tháng
        while (!current.isAfter(end)) {
            // Xác định ngày đầu và cuối tháng hiện tại
            LocalDateTime monthStart = current.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = current.atEndOfMonth().atTime(LocalTime.MAX);

            // Xử lý trường hợp tháng nằm lửng lơ ở đầu hoặc cuối khoảng chọn
            if (monthStart.isBefore(startDateTime)) monthStart = startDateTime;
            if (monthEnd.isAfter(endDateTime)) monthEnd = endDateTime;

            // Tính Doanh thu tháng (PaymentHistory dùng LocalDateTime)
            BigDecimal monthRev = paymentHistoryRepository.sumRevenueByStatusAndDateRange(
                    PaymentStatus.APPROVED.name(), monthStart, monthEnd);
            monthRev = Optional.ofNullable(monthRev).orElse(BigDecimal.ZERO);

            // Tính Chi phí tháng (Expense dùng LocalDate -> Cần convert)
            BigDecimal monthExp = expenseRepository.sumExpenseByDateRange(
                    monthStart.toLocalDate(), monthEnd.toLocalDate());
            monthExp = Optional.ofNullable(monthExp).orElse(BigDecimal.ZERO);

            // Thêm vào list
            details.add(MonthlyDetail.builder()
                    .month(current.getMonthValue() + "/" + current.getYear())
                    .revenue(monthRev.setScale(0, RoundingMode.HALF_UP))
                    .expense(monthExp.setScale(0, RoundingMode.HALF_UP))
                    .profit(monthRev.subtract(monthExp).setScale(0, RoundingMode.HALF_UP))
                    .build());


            // Next month
            current = current.plusMonths(1);
        }

        // 3. TRẢ VỀ KẾT QUẢ
        return ProfitReportResponse.builder()
                .startDate(startDateTime.toLocalDate())
                .endDate(endDateTime.toLocalDate())
                .totalRevenue(totalRevenue)
                .totalExpense(totalExpense)
                .netProfit(totalRevenue.subtract(totalExpense))
                .currency("USD")
                .monthlyBreakdown(details) // Danh sách cho biểu đồ
                .build();
    }
}