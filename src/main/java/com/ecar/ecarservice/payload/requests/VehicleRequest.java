package com.ecar.ecarservice.payload.requests;

public record VehicleRequest(
        Long carModelId,
        String licensePlate,
        String vinNumber) {
}