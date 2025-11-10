package com.ecar.ecarservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleDashboardInfoDTO {
    private String licensePlate;
    private String carType;
    private String carName;
    private String vinNumber;
    private LocalDateTime nextDate;
    private Long nextKm;
    private LocalDateTime currentDate;
    private Long currentKm;
}
