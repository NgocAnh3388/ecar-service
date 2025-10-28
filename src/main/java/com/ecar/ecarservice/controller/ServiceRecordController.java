package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @PostMapping("/admin/service-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<ServiceRecordResponseDto> createServiceRecord(@Valid @RequestBody CreateServiceRecordRequest request) {
        ServiceRecordResponseDto createdRecord = serviceRecordService.createServiceRecord(request);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @GetMapping("/service-records")
    public ResponseEntity<List<ServiceRecordResponseDto>> getServiceHistory(@RequestParam String licensePlate) {
        List<ServiceRecordResponseDto> history = serviceRecordService.getHistoryByLicensePlate(licensePlate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/admin/service-records/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ServiceRecordResponseDto> getServiceRecord(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRecordService.getServiceRecordById(id));
    }
}