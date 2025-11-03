package com.ecar.ecarservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SparePartCreateDTO {
    @NotNull private String partNumber;
    @NotNull private String partName;
    private String category;
    @Min(0) private Double unitPrice;
    @Min(0) private Integer stockQuantity;
    @Min(0) private Integer minStockLevel;
    @NotNull private Long carModelId;
}
