package com.ecar.ecarservice.payload.responses;

public record MilestoneResponse(
        Long id,
        Long kilometerAt,
        Long yearAt
) {
}
