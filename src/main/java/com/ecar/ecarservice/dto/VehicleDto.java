package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enitiies.CarModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleDto {
    private String licensePlate;
    private CarModel carModel;
    private String vinNumber;
    private Long nextKm;
    private LocalDateTime nextDate;
    private Long oldKm;
    private LocalDateTime oldDate;
}
