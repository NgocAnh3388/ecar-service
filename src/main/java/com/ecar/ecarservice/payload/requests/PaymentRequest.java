package com.ecar.ecarservice.payload.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Number of years cannot be null")
        @Min(value = 1, message = "Number of years must be at least 1")
        Long numOfYears
) {
}