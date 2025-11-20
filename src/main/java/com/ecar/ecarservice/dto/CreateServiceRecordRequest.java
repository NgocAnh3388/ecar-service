package com.ecar.ecarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

// DTO để nhận dữ liệu khi tạo một phiếu dịch vụ
@Getter
@Setter
public class CreateServiceRecordRequest {
    private Long bookingId; // ID của lịch hẹn gốc để liên kết
    private int kilometerReading; // Số km của xe tại thời điểm làm dịch vụ

    // --- Chi tiết Công việc đã làm ---
    // Dùng để tạo các bản ghi ServiceRecordDetail
    // Danh sách các hạng mục công việc đã thực hiện (cả kiểm tra và thay thế)
    // Ví dụ: [{itemName: "Kiểm tra hệ thống phanh", action: "INSPECT"},
    //         {itemName: "Dầu động cơ", action: "REPLACE"}]
    private List<ServiceDetailDto> serviceDetails;

    // --- Vật tư/Phụ tùng đã tiêu thụ ---
    // Dùng để tạo các bản ghi ServicePartUsage và TRỪ TỒN KHO
    // Ví dụ: [{partId: 101, quantity: 4}, {partId: 205, quantity: 1}]
    private List<UsedPartDto> usedParts; // Danh sách phụ tùng đã dùng

    private BigDecimal laborCost; // Chi phí tiền công
    private BigDecimal coveredByPackage; // Số tiền được gói bảo dưỡng/subscription chi trả

}