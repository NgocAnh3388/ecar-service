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

    // GHI CHÚ: Các endpoint GET và CREATE không có lỗi, giữ nguyên.
    @PostMapping("/history")
    public ResponseEntity<Page<MaintenanceHistoryDTO>> getMaintenanceHistory(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody MaintenanceHistorySearchRequest request) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceHistory(oidcUser, request));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createSchedule(@RequestBody MaintenanceScheduleRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        MaintenanceHistory newTicket = this.maintenanceService.createSchedule(request, oidcUser);
        Map<String, Long> response = Map.of("ticketId", newTicket.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
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

    @GetMapping("/technician/my-tasks")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<MaintenanceTicketResponse>> getMyTasks(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTicketsForTechnician(user));
    }


    // =================================================================================
    // GHI CHÚ: SỬA LỖI - HỢP NHẤT ENDPOINT
    // Endpoint này là duy nhất cho Staff/Admin để giao việc cho Technician.
    // Tên cũ '/service-create' sẽ bị loại bỏ. Hãy đảm bảo frontend của bạn gọi đến '/assign-task'.
    // =================================================================================
    @PostMapping("/assign-task")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Void> assignTaskToTechnician(@RequestBody ServiceCreateRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        this.maintenanceService.assignTaskToTechnician(request, oidcUser);
        return ResponseEntity.ok().build();
    }

    // =================================================================================
    // GHI CHÚ: SỬA LỖI - HỢP NHẤT ENDPOINT
    // Endpoint này là duy nhất cho Technician bấm nút "Hoàn thành công việc".
    // Nó sẽ gọi đến phương thức service đã được chuẩn hóa, bao gồm cả việc cập nhật lịch bảo dưỡng cho xe.
    // =================================================================================
    @PutMapping("/technician/tasks/{ticketId}/complete")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<MaintenanceHistoryDTO> completeTaskByTechnician(@PathVariable Long ticketId, @AuthenticationPrincipal OidcUser oidcUser) {
        MaintenanceHistoryDTO updatedDto = maintenanceService.completeTechnicianTask(ticketId, oidcUser);
        return ResponseEntity.ok(updatedDto);
    }
}
    // Ghi chú: Loại bỏ các endpoint thừa và gây nhầm lẫn như:
    // - /service-create (đã được thay bằng /assign-task)
    // - /{id}/technician-complete (đã được thay bằng /technician/tasks/{ticketId}/complete)
