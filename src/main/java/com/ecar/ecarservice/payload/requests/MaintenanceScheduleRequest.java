package com.ecar.ecarservice.payload.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record MaintenanceScheduleRequest(
        Long centerId,
        @JsonFormat(pattern="HH:mm")
        LocalTime scheduleTime,
        @JsonFormat(pattern="dd-MM-yyyy")
        LocalDate scheduleDate,
        Long vehicleId,
        Long numOfKm,
        Boolean isMaintenance,
        Boolean isRepair,
        String remark
) {
}