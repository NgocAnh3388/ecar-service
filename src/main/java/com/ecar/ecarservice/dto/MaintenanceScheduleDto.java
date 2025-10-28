package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceScheduleDto {
    private String itemName;
    private String category;
    private MaintenanceAction action; // "INSPECT" or "REPLACE"
    private BigDecimal price;
}