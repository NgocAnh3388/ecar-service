package com.ecar.ecarservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {
    @NotBlank(message = "Customer phone number cannot be blank")
    private String customerPhoneNumber;

    @NotBlank(message = "License plate cannot be blank")
    private String licensePlate;

    @NotBlank(message = "Car model cannot be blank")
    private String carModel;

    private String vinNumber; // Optional

    @NotBlank(message = "Service center cannot be blank")
    private String serviceCenter;

    @NotNull(message = "Appointment date and time cannot be null")
    @Future(message = "Appointment date and time must be in the future")
    private LocalDateTime appointmentDateTime;

    private String serviceAdvisor; // Optional

    private String notes; // Optional
}
