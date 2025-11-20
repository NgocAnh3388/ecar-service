package com.ecar.ecarservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UsedPartDetailDto {
    private String partName;
    private String partNumber;
    private int quantityUsed;
    private BigDecimal priceAtTimeOfUse;
}