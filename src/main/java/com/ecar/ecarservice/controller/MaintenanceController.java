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

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasRole('CUSTOMER')")
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

    // =================== STAFF: PHÂN CÔNG CÔNG VIỆC ===================
    @PostMapping("/service-create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> createService(@RequestBody ServiceCreateRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        this.maintenanceService.createService(request, oidcUser);
        return ResponseEntity.ok().build();
    }

    // ====================== HỦY PHIẾU (Đã sửa lỗi) ======================
    @PutMapping("/{id}/cancel")
    // @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Uncomment nếu cần bảo mật
    public ResponseEntity<?> cancelMaintenance(@PathVariable Long id) {
        maintenanceService.cancelMaintenance(id);
        // SỬA LỖI TẠI ĐÂY: Trả về message JSON đơn giản
        return ResponseEntity.ok(Collections.singletonMap("message", "Cancelled successfully"));
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

    @PostMapping("/{id}/technician-complete")
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

    public record UpdateUsedPartsRequest(List<UsedPartDto> usedParts) {}

    @PutMapping("/tasks/{ticketId}/used-parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<Void> updateUsedPartsForTask(
            @PathVariable Long ticketId,
            @RequestBody UpdateUsedPartsRequest request) {
        maintenanceService.updateUsedParts(ticketId, request.usedParts());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{ticketId}/used-parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<List<UsedPartDto>> getUsedPartsForTask(@PathVariable Long ticketId) {
        return ResponseEntity.ok(maintenanceService.getUsedParts(ticketId));
    }

    @PutMapping("/tasks/{ticketId}/additional-cost")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TECHNICIAN')")
    public ResponseEntity<Void> addOrUpdateAdditionalCost(
            @PathVariable Long ticketId,
            @RequestBody AdditionalCostRequest request) {
        maintenanceService.addOrUpdateAdditionalCost(ticketId, request.amount(), request.reason());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/handover")
    public ResponseEntity<?> handoverCar(@PathVariable Long id) {
        try {
            maintenanceService.handoverCarToCustomer(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Vehicle handed over successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/add-cost")
    public ResponseEntity<?> requestAdditionalCost(@RequestBody AdditionalCostRequest request) {
        maintenanceService.requestAdditionalCost(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Request sent to Staff successfully!"));
    }

    @PutMapping("/{id}/approval")
    public ResponseEntity<?> processCustomerDecision(@PathVariable Long id, @RequestParam String decision) {
        maintenanceService.processCustomerDecision(id, decision);
        return ResponseEntity.ok(Collections.singletonMap("message", "Order updated successfully based on customer decision."));
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> declineTask(@PathVariable Long id) {
        maintenanceService.declineTask(id);
        return ResponseEntity.ok().build();
    }
}