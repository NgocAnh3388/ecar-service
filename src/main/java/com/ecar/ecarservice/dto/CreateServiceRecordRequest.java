package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// DTO để nhận dữ liệu khi tạo một phiếu dịch vụ
@Getter
@Setter
public class CreateServiceRecordRequest {
    private Long bookingId;
    private int kilometerReading;
    private List<ServiceDetailDto> details;
}
