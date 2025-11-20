package com.ecar.ecarservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UsedPartHistoryDTO {
    private String partName;
    private String partNumber;
    private int quantityUsed;
    private BigDecimal priceAtTimeOfUse;
    private LocalDateTime serviceDate;
    private String licensePlate;
    private String centerName;
}