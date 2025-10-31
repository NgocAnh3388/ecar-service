package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {
    private String customerPhoneNumber;
    private String licensePlate;
    private String carModel;
    private String vinNumber;
    private String serviceCenter;
    private LocalDateTime appointmentDateTime;
    private String serviceAdvisor;
    private String notes;
}
