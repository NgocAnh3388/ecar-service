package com.ecar.ecarservice.payload.requests;

import jakarta.validation.constraints.Min;

public record PaymentRequest(
        @Min(value = 1, message = "Number of years must be at least 1")
        Long numOfYears
) {
}