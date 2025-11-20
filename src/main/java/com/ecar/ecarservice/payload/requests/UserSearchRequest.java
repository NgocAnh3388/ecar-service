package com.ecar.ecarservice.payload.requests;

import lombok.Data;
/**
 * DTO dùng để nhận các tham số tìm kiếm và phân trang
 * cho chức năng quản lý người dùng của Admin.
 */

@Data
public class UserSearchRequest {
    private String searchValue;
    private int page;
    private int size;
}