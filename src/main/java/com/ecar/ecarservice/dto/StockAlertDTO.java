package com.ecar.ecarservice.dto;

import lombok.Data;

@Data
public class StockAlertDTO {
    private Long id;
    private String partName;
    private String partNumber;
    private Integer currentStock;
    private Integer minimumStock;
    private Integer quantityToOrder;
    private String alertMessage;
    private Long carModelId;
    private String carModelName;
}
