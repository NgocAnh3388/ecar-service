package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ServiceRecordResponseDto {
    private Long id;
    private String licensePlate;
    private int kilometerReading;
    private LocalDateTime serviceDate;
    private String createdBy;
    private List<ServiceDetailDto> details;
    private BigDecimal totalPrice;
}