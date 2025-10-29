package com.ecar.ecarservice.service;


import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface MaintenanceService {

    Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request);
    void createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser);
}