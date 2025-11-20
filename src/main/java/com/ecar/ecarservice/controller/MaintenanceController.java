package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.AdditionalCostRequest;
import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.dto.UsedPartDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Quản lý toàn bộ quy trình dịch vụ bảo dưỡng/sửa chữa (phiếu dịch vụ nội bộ).
 */
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // =================== CUSTOMER: TẠO YÊU CẦU DỊCH VỤ MỚI ===================
    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createSchedule(@RequestBody MaintenanceScheduleRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        MaintenanceHistory newTicket = this.maintenanceService.createSchedule(request, oidcUser);
        Map<String, Long> response = Map.of("ticketId", newTicket.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // =================== CUSTOMER: XEM LỊCH SỬ ===================
    @PostMapping("/history")
    public ResponseEntity<Page<MaintenanceHistoryDTO>> getMaintenanceHistory(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody MaintenanceHistorySearchRequest request
    ) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceHistory(oidcUser, request));
    }

    // API cho Customer để duyệt chi phí
    @PostMapping("/tasks/{ticketId}/approve-cost")
    @PreAuthorize("hasRole('CUSTOMER')") // Chỉ customer mới được duyệt
    public ResponseEntity<Void> approveAdditionalCost(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal OidcUser oidcUser) {
        maintenanceService.approveAdditionalCost(ticketId, oidcUser);
        return ResponseEntity.ok().build();
    }

    // =================== ADMIN/STAFF: XEM TẤT CẢ CÁC PHIẾU ===================
    @RequestMapping("/all")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<List<MaintenanceTicketResponse>> getTickets(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTickets(user));
    }

    // =================== STAFF: PHÂN CÔNG CÔNG VIỆC CHO TECHNICIAN chọn mốc bảo dưỡng, dịch vụ sửa chữa, gán technician===================
    @PostMapping("/service-create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> createService(@RequestBody ServiceCreateRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        this.maintenanceService.createService(request, oidcUser);
        return ResponseEntity.ok().build();
    }
//    @PostMapping("/assign-task")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<Void> assignTaskToTechnician(@RequestBody ServiceCreateRequest request) {
//        this.maintenanceService.assignTask(request);
//        return ResponseEntity.ok().build();
//    }

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

    // =================== TECHNICIAN: XEM CÔNG VIỆC CỦA MÌNH ===================
    @GetMapping("/technician/my-tasks")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<MaintenanceTicketResponse>> getMyTasks(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTicketsForTechnician(user));
    }

    // =================== TECHNICIAN: BÁO CÁO HOÀN THÀNH KỸ THUẬT ===================
    @PutMapping("/technician/tasks/{ticketId}/complete")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> completeService(@PathVariable Long ticketId, @AuthenticationPrincipal OidcUser oidcUser) {
        maintenanceService.completeServiceByTechnician(ticketId, oidcUser);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/technician-complete") // <-- SỬA THÀNH @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN', 'STAFF')")
    public ResponseEntity<MaintenanceHistoryDTO> completeTaskByTechnician(
            @PathVariable Long id,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        try {
            MaintenanceHistoryDTO updatedDto = maintenanceService.completeServiceByTechnician(id, oidcUser);
            return ResponseEntity.ok(updatedDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ==========================================================
    // == API CHUNG (STAFF, ADMIN, TECHNICIAN)
    // ==========================================================
//    Lấy danh sách các mốc bảo dưỡng cho một dòng xe.
    @GetMapping("/milestone/{carModelId}")
    public ResponseEntity<List<MilestoneResponse>> getMilestone(@PathVariable Long carModelId) {
        return ResponseEntity.ok(this.maintenanceService.getMilestone(carModelId));
    }

//    Lấy checklist dịch vụ bảo dưỡng mặc định theo dòng xe và mốc bảo dưỡng.
    @GetMapping("/service-group/{carModelId}/{milestoneId}")
    public ResponseEntity<List<ServiceGroup>> getMaintenanceServiceGroupByCarModelIdAndMilestoneId(@PathVariable Long carModelId,
                                                                                                   @PathVariable Long milestoneId) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceServiceGroup(carModelId, milestoneId));
    }

//    Lấy checklist các dịch vụ sửa chữa (cùng với các dịch vụ đã chọn) cho một phiếu.
    @GetMapping("/service-group/{ticketId}")
    public ResponseEntity<List<ServiceGroup>> getServiceGroup(@PathVariable Long ticketId) {
        return ResponseEntity.ok(this.maintenanceService.getServiceGroup(ticketId));
    }

        public record UpdateUsedPartsRequest(List<UsedPartDto> usedParts) {}

    //    Cập nhật danh sách phụ tùng dự kiến sẽ sử dụng cho một phiếu dịch vụ.
    @PutMapping("/tasks/{ticketId}/used-parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<Void> updateUsedPartsForTask(
            @PathVariable Long ticketId,
            @RequestBody UpdateUsedPartsRequest request) {
        maintenanceService.updateUsedParts(ticketId, request.usedParts());
        return ResponseEntity.ok().build();
    }

    //    Lấy danh sách phụ tùng dự kiến đã được lưu cho một phiếu dịch vụ.
    @GetMapping("/tasks/{ticketId}/used-parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<UsedPartDto>> getUsedPartsForTask(@PathVariable Long ticketId) {
        return ResponseEntity.ok(maintenanceService.getUsedParts(ticketId));
    }

    //    Thêm hoặc cập nhật chi phí phát sinh cho một phiếu dịch vụ.
    @PutMapping("/tasks/{ticketId}/additional-cost")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<Void> addOrUpdateAdditionalCost(
            @PathVariable Long ticketId,
            @RequestBody AdditionalCostRequest request) {
        maintenanceService.addOrUpdateAdditionalCost(ticketId, request.getAmount(), request.getReason());
        return ResponseEntity.ok().build();
    }

}
