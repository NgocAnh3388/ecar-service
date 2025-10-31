package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// DTO để trả về dữ liệu lịch sử cho người dùng
@Getter
@Setter
public class ServiceRecordResponseDto {
    private Long id;
    private String licensePlate;
    private int kilometerReading;
    private LocalDateTime serviceDate;
    private String createdBy;
    private List<ServiceDetailDto> details;
}

