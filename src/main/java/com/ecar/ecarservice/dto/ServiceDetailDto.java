package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceDetailDto {
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;

    @NotNull(message = "Action cannot be null")
    private MaintenanceAction action;

    private String notes;
    private BigDecimal price;
}