package com.ecar.ecarservice.payload.requests;

import java.util.List;
/**
 * DTO dùng để nhận dữ liệu khi Staff/Admin phân công công việc cho một Technician.
 * Chứa thông tin về phiếu dịch vụ, các hạng mục cần làm, và kỹ thuật viên được giao.
 */

public record ServiceCreateRequest(
        Long ticketId,
        Long numOfKm,
        Long scheduleId, // ID của mốc bảo dưỡng (maintenance_milestone)
        Long technicianId,
        List<Long> checkedServiceIds // ID của các dịch vụ sửa chữa (type 'F') được chọn thêm
) {
}
