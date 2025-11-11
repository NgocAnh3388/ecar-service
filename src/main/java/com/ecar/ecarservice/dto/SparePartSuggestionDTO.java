package com.ecar.ecarservice.dto;

import lombok.Data;

@Data // Lombok
public class SparePartSuggestionDTO {
    private Long partId;
    private String partName;
    private String partNumber;
    private Double unitPrice;
    private int stockQuantity;
    private int defaultQuantity; // Số lượng gợi ý
    private Long serviceId; // Thuộc về dịch vụ nào
}
