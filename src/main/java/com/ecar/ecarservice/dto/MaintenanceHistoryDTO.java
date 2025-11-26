package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//DTO dùng để hiển thị thông tin tóm tắt của một phiếu dịch vụ trong danh sách lịch sử của khách hàng.
@Data
@Builder
public class MaintenanceHistoryDTO {
    private Long id;
    private Long bookingId;
    private String carName;

    private String carType;

    private String licensePlate;

    private LocalDateTime submittedAt;

    private LocalDateTime completedAt;

    private MaintenanceStatus status;
}
