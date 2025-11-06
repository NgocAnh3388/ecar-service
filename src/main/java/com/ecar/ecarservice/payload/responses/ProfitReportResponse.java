package com.ecar.ecarservice.payload.responses;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ProfitReportResponse(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRevenue,  // Tổng doanh thu
        BigDecimal totalExpense,  // Tổng chi phí
        BigDecimal netProfit,     // Lợi nhuận ròng
        String currency
) {
}