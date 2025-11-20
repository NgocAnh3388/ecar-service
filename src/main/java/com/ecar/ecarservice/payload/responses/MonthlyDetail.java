package com.ecar.ecarservice.payload.responses;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MonthlyDetail(
        String month,       // Ví dụ: "11/2024"
        BigDecimal revenue, // Doanh thu tháng đó
        BigDecimal expense, // Chi phí tháng đó
        BigDecimal profit   // Lợi nhuận tháng đó
) {}