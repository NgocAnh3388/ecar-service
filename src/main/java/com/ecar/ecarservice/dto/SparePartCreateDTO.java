package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.entities.CarModel;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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

    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    @NotNull(message = "Minimum stock level cannot be null")
    @Min(value = 0, message = "Minimum stock level must be non-negative")
    private Integer minStockLevel;

    @NotNull(message = "Car model ID cannot be null")
    private Long carModelId;

}
