package com.ecar.ecarservice.payload.responses;

import java.time.LocalDateTime;

public record SubscriptionInfoResponse(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime paymentDate) {
    }