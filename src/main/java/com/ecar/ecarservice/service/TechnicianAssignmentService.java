package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianAssignmentService {

    private final AppUserRepository appUserRepository;
    private final MaintenanceHistoryRepository maintenanceHistoryRepository;

    @Value("${technician.max.concurrent.services:3}")
    private int maxServicesPerTechnician;

    public List<AppUser> getAvailableTechniciansByCenter(Long centerId) {

        // 1. Lấy tất cả technician thuộc trung tâm này (Giữ nguyên)
        List<AppUser> allTechniciansInCenter = appUserRepository
                .findByRolesContainingAndCenterId(AppRole.TECHNICIAN, centerId);

        //Nếu không có tech nào, trả về luôn
        if (allTechniciansInCenter.isEmpty()) {
            return allTechniciansInCenter;
        }

        //Lấy danh sách ID
        List<Long> technicianIds = allTechniciansInCenter.stream()
                .map(AppUser::getId)
                .collect(Collectors.toList());

        //Chỉ đếm tải cho các technician trong center này
        Map<Long, Long> technicianLoad = maintenanceHistoryRepository
                .getTechnicianLoadByStatusAndIds(MaintenanceStatus.TECHNICIAN_RECEIVED, technicianIds)
                .stream()
                .collect(Collectors.toMap(
                        MaintenanceHistoryRepository.TechnicianLoad::getTechnicianId,
                        MaintenanceHistoryRepository.TechnicianLoad::getLoadCount
                ));

        //Lọc danh sách
        return allTechniciansInCenter.stream()
                .filter(technician -> {
                    long currentLoad = technicianLoad.getOrDefault(technician.getId(), 0L);
                    return currentLoad < maxServicesPerTechnician;
                })
                .collect(Collectors.toList());
    }
}