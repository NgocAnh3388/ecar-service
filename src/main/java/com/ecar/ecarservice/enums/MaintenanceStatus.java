package com.ecar.ecarservice.enums;

public enum MaintenanceStatus {
    // Khách hàng vừa đặt lịch
    CUSTOMER_SUBMITTED,

    // Technician đã nhận xe và đang làm việc
    TECHNICIAN_RECEIVED,

    // Technician phát hiện vấn đề -> Chờ khách duyệt chi phí phát sinh
    // (Bạn có thể chọn 1 trong 2 tên: PENDING_APPROVAL hoặc CUSTOMER_APPROVAL_PENDING)
    // Tôi khuyên dùng PENDING_APPROVAL cho ngắn gọn giống FE tôi đã viết
    PENDING_APPROVAL,

    // Khách hàng đã duyệt (Trạng thái trung gian nếu cần, hoặc quay lại RECEIVED)
    CUSTOMER_APPROVED,

    // Technician đã hoàn thành công việc
    TECHNICIAN_COMPLETED,

    // Staff đã giao xe xong -> Hoàn tất quy trình
    DONE,

    // Đơn bị hủy (Do khách hoặc do từ chối chi phí)
    CANCELLED
}
