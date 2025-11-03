package com.ecar.ecarservice.dto;

import lombok.Data;

@Data
public class SparePartDTO {
    private Long id;
    private String partNumber;
    private String partName;
    private String category;
    private Double unitPrice;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private Long carModelId;
    private String carModelName;
}
