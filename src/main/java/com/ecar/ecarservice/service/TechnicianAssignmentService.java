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

    // Cấu hình số lượng dịch vụ tối đa cho mỗi technician
    // Giá trị này có thể được đặt trong file application.properties
    // Ví dụ: technician.max.concurrent.services=3
    @Value("${technician.max.concurrent.services:3}")
    private int maxServicesPerTechnician;

    /**
     * Lấy danh sách các Kỹ thuật viên (TECHNICIAN) có sẵn tại một trung tâm,
     * đồng thời lọc ra những người không bị quá tải.
     *
     * @param centerId ID của trung tâm (của dịch vụ bảo dưỡng)
     * @return Danh sách AppUser (là Technician) còn trống slot
     */
    public List<AppUser> getAvailableTechniciansByCenter(Long centerId) {

        // 1. Lấy tất cả technician thuộc trung tâm này
        List<AppUser> allTechniciansInCenter = appUserRepository
                .findByRolesContainsAndCenterId(AppRole.TECHNICIAN, centerId);

        // 2. Lấy tất cả các dịch vụ đang được xử lý (TECHNICIAN_RECEIVED)
        // Đây là các dịch vụ "active" dùng để tính tải
        List<MaintenanceHistory> activeServices = maintenanceHistoryRepository
                .findByStatus(MaintenanceStatus.TECHNICIAN_RECEIVED);

        // 3. Đếm số lượng dịch vụ active cho mỗi technician
        // Map<TechnicianId, Count>
        Map<Long, Long> technicianLoad = activeServices.stream()
                .filter(service -> service.getTechnician() != null && service.getTechnician().getId() != null)
                .collect(Collectors.groupingBy(
                        service -> service.getTechnician().getId(),
                        Collectors.counting()
                ));

        // 4. Lọc danh sách technician của trung tâm
        return allTechniciansInCenter.stream()
                .filter(technician -> {
                    // Lấy số lượng dịch vụ hiện tại của technician này
                    long currentLoad = technicianLoad.getOrDefault(technician.getId(), 0L);

                    // Chỉ giữ lại technician nào có tải hiện tại < mức tối đa
                    return currentLoad < maxServicesPerTechnician;
                })
                .collect(Collectors.toList());
    }
}