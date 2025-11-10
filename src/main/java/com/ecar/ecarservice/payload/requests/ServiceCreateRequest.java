package com.ecar.ecarservice.payload.requests;

import java.util.List;

public record ServiceCreateRequest(
        Long ticketId,
        Long numOfKm,
        Long scheduleId,
        Long technicianId,
        List<Long> checkedServiceIds
) {
}
