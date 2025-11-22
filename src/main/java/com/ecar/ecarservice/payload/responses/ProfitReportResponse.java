package com.ecar.ecarservice.payload.responses;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // Import List

@Builder
public record ProfitReportResponse(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRevenue,
        BigDecimal totalExpense,
        BigDecimal netProfit,
        String currency,
        List<MonthlyDetail> monthlyBreakdown
) {
}