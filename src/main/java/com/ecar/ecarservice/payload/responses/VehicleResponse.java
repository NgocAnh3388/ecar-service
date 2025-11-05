package com.ecar.ecarservice.payload.responses;

import com.ecar.ecarservice.entities.CarModel;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String licensePlate,
        CarModelResponse carModel,
        String vinNumber,
        Long nextKm,
        LocalDateTime nextDate,
        Long oldKm,
        LocalDateTime oldDate
) {
}
