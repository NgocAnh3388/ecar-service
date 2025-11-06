package com.ecar.ecarservice.payload.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotNull(message = "Car model ID is required")
        Long carModelId,

        @NotBlank(message = "License plate is required")
        String licensePlate,

        String vinNumber) {
}