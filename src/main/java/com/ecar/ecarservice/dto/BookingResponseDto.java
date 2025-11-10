package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {
    private Long id;
    private String customerPhoneNumber;
    private String licensePlate;
    private String carModel;
    private String vinNumber;
    private String serviceCenter;
    private LocalDateTime appointmentDateTime;
    private String serviceAdvisor;
    private String notes;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private UserInBookingDto user;

    @Getter
    @Setter
    public static class UserInBookingDto {
        private Long id;
        private String email;
    }
}
