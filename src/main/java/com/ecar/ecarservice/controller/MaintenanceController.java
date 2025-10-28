package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import com.ecar.ecarservice.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<MaintenanceScheduleDto>> getSchedule(@RequestParam String model, @RequestParam int kilometers) {
        List<MaintenanceScheduleDto> schedule = maintenanceService.getScheduleByKilometers(model, kilometers);
        return ResponseEntity.ok(schedule);
    }
}