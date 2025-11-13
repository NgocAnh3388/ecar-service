package com.ecar.ecarservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateDTO {
    @NotNull private Boolean isAddition;
    @Min(1) private Integer quantityChange;

    private Integer minStockLevel;
}
