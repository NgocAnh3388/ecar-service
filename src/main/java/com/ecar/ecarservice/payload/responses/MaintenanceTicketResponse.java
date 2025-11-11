package com.ecar.ecarservice.payload.responses;

import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MaintenanceTicketResponse(
        Long id,
        String customerName,
        Long carModelId,
        String carName,
        String licensePlate,
        Long numOfKm,
        LocalDateTime submittedAt,
        String staffName,
        Long staffId,
        LocalDateTime staffReceivedAt,
        String technicianName,
        Long technicianId,
        LocalDateTime technicianReceivedAt,
        LocalDateTime completedAt,
        MaintenanceStatus status,
        String centerName,
        LocalDate scheduleDate,
        LocalTime scheduleTime,
        Long scheduleId,

        // Đặt annotation trực tiếp, không có private, không có dấu chấm phẩy
        @JsonProperty("isMaintenance")
        Boolean isMaintenance,

        @JsonProperty("isRepair")
        Boolean isRepair
) { }
