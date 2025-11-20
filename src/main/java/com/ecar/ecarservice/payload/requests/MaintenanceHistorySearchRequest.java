package com.ecar.ecarservice.payload.requests;

import lombok.Data;
/**
 * DTO dùng để nhận các tham số tìm kiếm và phân trang
 * cho chức năng xem lịch sử bảo dưỡng của khách hàng.
 */

@Data
public class MaintenanceHistorySearchRequest {
    private String searchValue;
    private int page;
    private int size;
}
