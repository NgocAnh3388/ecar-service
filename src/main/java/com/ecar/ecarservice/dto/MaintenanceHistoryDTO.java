package com.ecar.ecarservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MaintenanceHistoryDTO {
    private String carName;

    private String carType;

    private String licensePlate;

    private LocalDateTime submittedAt;

    private LocalDateTime completedAt;

    private String status;
}