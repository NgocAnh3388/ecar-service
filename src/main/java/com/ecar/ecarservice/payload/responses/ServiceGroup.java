package com.ecar.ecarservice.payload.responses;

import java.util.List;

public record ServiceGroup(
        String category,
        List<ServiceItem> items
) {
}
