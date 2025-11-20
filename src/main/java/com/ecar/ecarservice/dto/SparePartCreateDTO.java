package com.ecar.ecarservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO dùng để nhận dữ liệu khi tạo mới hoặc cập nhật thông tin chung
 * của một loại phụ tùng (master data).
 * Không chứa thông tin tồn kho.
 */
@Data
public class SparePartCreateDTO {
    @NotBlank(message = "Part number cannot be blank")
    private String partNumber;

    @NotBlank(message = "Part name cannot be blank")
    private String partName;

    private String category;

    @NotNull(message = "Unit price cannot be null")
    @Min(value = 0, message = "Unit price must be non-negative")
    private Double unitPrice;

//    @NotNull(message = "Stock quantity cannot be null")
//    @Min(value = 0, message = "Stock quantity must be non-negative")
//    private Integer stockQuantity;
//
//    @NotNull(message = "Minimum stock level cannot be null")
//    @Min(value = 0, message = "Minimum stock level must be non-negative")
//    private Integer minStockLevel;

    @NotNull(message = "Car model ID cannot be null")
    private Long carModelId;

}
