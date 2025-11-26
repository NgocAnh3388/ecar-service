package com.ecar.ecarservice.service;

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
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface MaintenanceService {
    Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request);
    MaintenanceHistory createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser);
    List<MaintenanceTicketResponse> getTickets(OidcUser user);
    List<MilestoneResponse> getMilestone(Long carModelId);

    List<ServiceGroup> getMaintenanceServiceGroup(Long modelId,Long maintenanceScheduleId);

    List<ServiceGroup> getServiceGroup(Long ticketId);

    void createService(ServiceCreateRequest request, OidcUser oidcUser);

    List<MaintenanceTicketResponse> getTicketsForTechnician(OidcUser user);

    @Transactional
    MaintenanceHistoryDTO completeServiceByTechnician(Long ticketId, OidcUser oidcUser);

    MaintenanceHistoryDTO completeTechnicianTask(Long maintenanceId);

    void cancelMaintenance(Long id);

    void reopenMaintenance(Long id);
    /**
     * Cập nhật danh sách phụ tùng dự kiến sử dụng cho một phiếu dịch vụ (task).
     * @param ticketId ID của phiếu dịch vụ (MaintenanceHistory).
     * @param usedParts Danh sách các phụ tùng và số lượng.
     */
    void updateUsedParts(Long ticketId, List<UsedPartDto> usedParts);

    /**
     * Lấy danh sách phụ tùng dự kiến đã được lưu cho một phiếu dịch vụ.
     * @param ticketId ID của phiếu dịch vụ.
     * @return Danh sách các phụ tùng và số lượng.
     */
    List<UsedPartDto> getUsedParts(Long ticketId);

    /**
     * Staff/Technician thêm hoặc cập nhật chi phí phát sinh cho một phiếu dịch vụ.
     */
    void addOrUpdateAdditionalCost(Long ticketId, BigDecimal amount, String reason);

    /**
     * Customer duyệt chi phí phát sinh.
     */
    void approveAdditionalCost(Long ticketId, OidcUser oidcUser);
    void handoverCarToCustomer(Long id);

    void requestAdditionalCost(AdditionalCostRequest request);
    void processCustomerDecision(Long id, String decision);
    void declineTask(Long id);

}
