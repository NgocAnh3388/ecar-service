package com.ecar.ecarservice.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) dùng để nhận dữ liệu từ client
 * khi một nhân viên thêm hoặc cập nhật chi phí phát sinh cho một phiếu dịch vụ.
 */

@Data
public class AdditionalCostRequest {
    private BigDecimal amount;
    private String reason;
}
