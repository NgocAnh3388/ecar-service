package com.ecar.ecarservice.payload.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
/**
 * DTO dùng để nhận dữ liệu khi khách hàng tạo một yêu cầu/phiếu dịch vụ mới.
 * Đây là điểm khởi đầu cho một quy trình bảo dưỡng/sửa chữa.
 */

public record MaintenanceScheduleRequest(
        Long centerId,
        @JsonFormat(pattern="HH:mm")
        LocalTime scheduleTime,
        @JsonFormat(pattern="dd-MM-yyyy")
        LocalDate scheduleDate,
        Long vehicleId,
        Long numOfKm,
        Boolean isMaintenance,
        Boolean isRepair,
        String remark
) {
}