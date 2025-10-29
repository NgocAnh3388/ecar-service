package com.ecar.ecarservice.payload.responses;

public record CarModelResponse(
        Long id,
        String carName,
        String carType
) {
}