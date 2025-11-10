package com.ecar.ecarservice.payload.requests;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingRequest(
        String customerName,
        String licensePlate,
        String carModelName,
        String centerName,
        LocalDateTime scheduledAt,
        String email
) {
}
