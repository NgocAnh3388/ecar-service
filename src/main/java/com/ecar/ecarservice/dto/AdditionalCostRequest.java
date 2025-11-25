package com.ecar.ecarservice.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) dùng để nhận dữ liệu từ client
 * khi một nhân viên thêm hoặc cập nhật chi phí phát sinh cho một phiếu dịch vụ.
 */

public record AdditionalCostRequest(
        Long ticketId,
        BigDecimal amount,
        String reason
) {}
