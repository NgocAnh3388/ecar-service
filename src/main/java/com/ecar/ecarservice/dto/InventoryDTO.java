package com.ecar.ecarservice.dto;

import lombok.Data;

//DTO này đại diện cho thông tin tồn kho của một phụ tùng tại một center cụ thể
@Data
public class InventoryDTO {
    private Long id; // ID của bản ghi inventory
    private String centerName;
    private Long partId;
    private String partName;
    private String partNumber;
    private int stockQuantity;
    private int minStockLevel;
}
