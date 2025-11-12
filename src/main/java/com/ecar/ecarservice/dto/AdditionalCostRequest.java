package com.ecar.ecarservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdditionalCostRequest {
    private BigDecimal amount;
    private String reason;
}
