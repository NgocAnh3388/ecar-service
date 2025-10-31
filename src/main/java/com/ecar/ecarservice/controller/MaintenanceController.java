package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import com.ecar.ecarservice.service.MaintenanceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public ResponseEntity<Page<MaintenanceHistoryDTO>> getMaintenanceHistory(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody MaintenanceHistorySearchRequest request
    ) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceHistory(oidcUser, request));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Void> createSchedule(@RequestBody MaintenanceScheduleRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        this.maintenanceService.createSchedule(request, oidcUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/all")
    public ResponseEntity<List<MaintenanceTicketResponse>> getTickets(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(this.maintenanceService.getTickets(user));
    }

    @RequestMapping(value = "/milestone/{carModelId}", method = RequestMethod.GET)
    public ResponseEntity<List<MilestoneResponse>> getMilestone(@PathVariable Long carModelId) {
        return ResponseEntity.ok(this.maintenanceService.getMilestone(carModelId));
    }

    @RequestMapping(value = "/service-group/{carModelId}/{milestoneId}", method = RequestMethod.GET)
    public ResponseEntity<List<ServiceGroup>> getMaintenanceServiceGroupByCarModelIdAndMilestoneId(@PathVariable Long carModelId,
                                                                                                   @PathVariable Long milestoneId) {
        return ResponseEntity.ok(this.maintenanceService.getMaintenanceServiceGroup(carModelId, milestoneId));
    }

    @RequestMapping(value = "/service-group/{ticketId}", method = RequestMethod.GET)
    public ResponseEntity<List<ServiceGroup>> getServiceGroup(@PathVariable Long ticketId) {
        return ResponseEntity.ok(this.maintenanceService.getServiceGroup(ticketId));
    }

    @RequestMapping(value = "/service-create", method = RequestMethod.POST)
    public ResponseEntity<Void> createService(@AuthenticationPrincipal OidcUser oidcUser, @RequestBody ServiceCreateRequest request) {
        this.maintenanceService.createService(request, oidcUser);
        return ResponseEntity.ok().build();
    }
}
