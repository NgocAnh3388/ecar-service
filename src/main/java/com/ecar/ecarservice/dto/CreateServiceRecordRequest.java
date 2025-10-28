package com.ecar.ecarservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateServiceRecordRequest {
    @NotNull(message = "Booking ID cannot be null")
    private Long bookingId;

    @NotNull(message = "Kilometer reading cannot be null")
    @Positive(message = "Kilometer reading must be a positive number")
    private Integer kilometerReading;

    @NotEmpty(message = "Service details cannot be empty")
    private List<@Valid ServiceDetailDto> details;
}