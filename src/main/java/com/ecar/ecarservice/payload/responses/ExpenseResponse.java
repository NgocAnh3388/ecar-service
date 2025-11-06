package com.ecar.ecarservice.payload.responses;

import com.ecar.ecarservice.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        ExpenseCategory category,
        LocalDate expenseDate,
        String createdBy
) {
}