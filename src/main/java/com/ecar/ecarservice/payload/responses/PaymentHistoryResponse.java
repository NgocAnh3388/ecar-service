package com.ecar.ecarservice.payload.responses;

import java.time.LocalDateTime;

public record PaymentHistoryResponse(
        LocalDateTime paymentDate,
        String paymentMethod,
        String paymentStatus,
        Long numOfYears) {
}