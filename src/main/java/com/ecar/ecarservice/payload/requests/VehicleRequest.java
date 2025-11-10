package com.ecar.ecarservice.payload.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
        @NotNull(message = "Car model ID cannot be null")
        Long carModelId,

        @NotBlank(message = "License plate cannot be blank")
        @Size(min = 4, max = 15, message = "License plate must be between 4 and 15 characters")
        String licensePlate,

        @NotBlank(message = "VIN number cannot be blank")
        @Size(min = 17, max = 17, message = "VIN number must be exactly 17 characters")
        String vinNumber
) {
}