package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MaintenanceService {
    Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request);
    MaintenanceHistory createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser);
    List<MaintenanceTicketResponse> getTickets(OidcUser user);
    List<MilestoneResponse> getMilestone(Long carModelId);
    List<ServiceGroup> getMaintenanceServiceGroup(Long modelId, Long maintenanceScheduleId);
    List<ServiceGroup> getServiceGroup(Long ticketId);
    List<MaintenanceTicketResponse> getTicketsForTechnician(OidcUser user);
    void assignTaskToTechnician(ServiceCreateRequest request, OidcUser oidcUser);
    MaintenanceHistoryDTO completeTechnicianTask(Long ticketId, OidcUser oidcUser);
    void finalizeAndHandOver(Long ticketId);


    @Transactional
    void completeServiceByTechnician(Long ticketId, OidcUser oidcUser);

    MaintenanceHistoryDTO completeTechnicianTask(Long maintenanceId);
}
