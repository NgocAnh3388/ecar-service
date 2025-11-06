package com.ecar.ecarservice.payload.requests;

import com.ecar.ecarservice.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank(message = "Mô tả không được để trống")
        String description,

        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
        BigDecimal amount,

        @NotNull(message = "Phân loại không được để trống")
        ExpenseCategory category,

        @NotNull(message = "Ngày chi không được để trống")
        LocalDate expenseDate
) {
}