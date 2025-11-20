package com.ecar.ecarservice.payload.requests;

import com.ecar.ecarservice.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * DTO dùng để nhận dữ liệu khi Admin tạo một khoản chi phí vận hành mới.
 * Sử dụng validation để đảm bảo dữ liệu đầu vào hợp lệ.
 */

public record ExpenseRequest(
        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Amount cannot be blank")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Category cannot be blank")
        ExpenseCategory category,

        @NotNull(message = "Expense date cannot be blank")
        LocalDate expenseDate
) {
}