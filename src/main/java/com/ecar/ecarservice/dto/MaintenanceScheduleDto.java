package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceScheduleDto {
    private String itemName;
    private String category;
    private MaintenanceAction action; // "CHECK" or "REPLACE"
}