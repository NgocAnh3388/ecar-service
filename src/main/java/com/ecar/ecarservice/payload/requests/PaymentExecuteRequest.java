package com.ecar.ecarservice.payload.requests;

public record PaymentExecuteRequest(
        String paymentId,
        String payerId
) {
}
