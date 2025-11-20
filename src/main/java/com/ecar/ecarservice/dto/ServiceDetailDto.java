package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.MaintenanceAction;
import lombok.Getter;
import lombok.Setter;

// DTO con, mô tả một hạng mục công việc cụ thể đã được thực hiện
// trong một lần dịch vụ (ví dụ: "Kiểm tra phanh", "Thay dầu").
@Getter
@Setter
public class ServiceDetailDto {
    private String itemName;
    private MaintenanceAction action; // INSPECT hoặc REPLACE
    private String notes;
}
