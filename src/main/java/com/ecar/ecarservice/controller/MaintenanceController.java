package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import com.ecar.ecarservice.service.MaintenanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/history")
    public ResponseEntity<Page<MaintenanceHistoryDTO>> getMaintenanceHistory(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody MaintenanceHistorySearchRequest request
    ) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceHistory(oidcUser, request));
    }

    @PostMapping("/create") // Dùng @PostMapping cho gọn
    public ResponseEntity<Map<String, Long>> createSchedule(@RequestBody MaintenanceScheduleRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        MaintenanceHistory newTicket = this.maintenanceService.createSchedule(request, oidcUser);

        // Trả về một đối tượng JSON chứa ID
        Map<String, Long> response = Map.of("ticketId", newTicket.getId());

        // Trả về status 201 Created cùng với ID
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping("/all")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN','TECHNICIAN')")
    public ResponseEntity<List<MaintenanceTicketResponse>> getTickets(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTickets(user));
    }

    @GetMapping("/milestone/{carModelId}")
    public ResponseEntity<List<MilestoneResponse>> getMilestone(@PathVariable Long carModelId) {
        return ResponseEntity.ok(this.maintenanceService.getMilestone(carModelId));
    }

    @GetMapping("/service-group/{carModelId}/{milestoneId}")
    public ResponseEntity<List<ServiceGroup>> getMaintenanceServiceGroupByCarModelIdAndMilestoneId(@PathVariable Long carModelId,
                                                                                                   @PathVariable Long milestoneId) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceServiceGroup(carModelId, milestoneId));
    }

    @GetMapping("/service-group/{ticketId}")
    public ResponseEntity<List<ServiceGroup>> getServiceGroup(@PathVariable Long ticketId) {
        return ResponseEntity.ok(this.maintenanceService.getServiceGroup(ticketId));
    }

    @PostMapping("/service-create")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN','TECHNICIAN')")
    public ResponseEntity<Void> createService(@AuthenticationPrincipal OidcUser oidcUser, @RequestBody ServiceCreateRequest request) {
        this.maintenanceService.createService(request, oidcUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/technician/my-tasks")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<MaintenanceTicketResponse>> getMyTasks(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTicketsForTechnician(user));
    }

    @PutMapping("/technician/tasks/{ticketId}/complete")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> completeService(@PathVariable Long ticketId, @AuthenticationPrincipal OidcUser oidcUser) {
        maintenanceService.completeServiceByTechnician(ticketId, oidcUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/technician-complete") // Dùng @PutMapping sẽ đúng ngữ nghĩa hơn là @PostMapping
    // Sửa thành hasAnyRole và bỏ tiền tố 'ROLE_'
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'STAFF')")
    public ResponseEntity<MaintenanceHistoryDTO> completeTaskByTechnician(@PathVariable Long id) { // Đổi tên để tránh trùng lặp
        try {
            // Gọi phương thức trong service mà chúng ta vừa implement
            MaintenanceHistoryDTO updatedDto = maintenanceService.completeTechnicianTask(id);
            // Nếu thành công, trả về 200 OK cùng với dữ liệu đã được cập nhật
            return ResponseEntity.ok(updatedDto);
        } catch (EntityNotFoundException e) {
            // Nếu service ném ra lỗi không tìm thấy, trả về 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    // ====================== HỦY PHIẾU ======================
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> cancelMaintenance(@PathVariable Long id) {
        maintenanceService.cancelMaintenance(id);
        return ResponseEntity.ok().build();
    }

    // ====================== KÍCH HOẠT LẠI PHIẾU ======================
    @PutMapping("/{id}/reopen")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> reopenMaintenance(@PathVariable Long id) {
        maintenanceService.reopenMaintenance(id);
        return ResponseEntity.ok().build();
    }

}
