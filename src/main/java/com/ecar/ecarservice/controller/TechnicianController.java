package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.dto.ServiceDetailDto;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.BookingService;
import com.ecar.ecarservice.service.ServiceRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technician")
@PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN')")
public class TechnicianController {

    private final BookingService bookingService;
    private final ServiceRecordService serviceRecordService;
    private final AppUserRepository appUserRepository;


    public TechnicianController(BookingService bookingService, ServiceRecordService serviceRecordService, AppUserRepository appUserRepository) {
        this.bookingService = bookingService;
        this.serviceRecordService = serviceRecordService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<BookingResponseDto>> getMyTasks(@AuthenticationPrincipal OidcUser oidcUser) {
        AppUser technician = appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        List<BookingResponseDto> tasks = bookingService.getTasksForTechnician(technician.getId());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/service-records/{recordId}/details")
    public ResponseEntity<ServiceRecordResponseDto> addServiceDetail(@PathVariable Long recordId, @RequestBody ServiceDetailDto detailDto) {

        return ResponseEntity.ok(serviceRecordService.addDetailToRecord(recordId, detailDto));
    }

}