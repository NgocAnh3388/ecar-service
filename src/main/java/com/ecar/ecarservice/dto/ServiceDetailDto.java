package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceAction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDetailDto {
    private String itemName;
    private MaintenanceAction action;
    private String notes;
}
