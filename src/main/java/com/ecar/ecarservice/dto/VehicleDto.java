package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleDto {
    private Long id;
    private String licensePlate;
    private String carModel;
    private String vinNumber;

    private boolean active;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}