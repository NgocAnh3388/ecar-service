package com.ecar.ecarservice.payload.responses;

public record ServiceItem(
        Long id,
        String serviceName,
        boolean isChecked
) {
}
