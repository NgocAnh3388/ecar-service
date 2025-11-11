package com.ecar.ecarservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubscriptionInfoDto {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime paymentDate;
}
