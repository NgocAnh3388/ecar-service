package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import com.ecar.ecarservice.repositories.MaintenanceScheduleRepository;
import com.ecar.ecarservice.service.MaintenanceService;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceScheduleRepository scheduleRepository;

    public MaintenanceServiceImpl(MaintenanceScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<MaintenanceScheduleDto> getScheduleByKilometers(int kilometers) {
        // 1. Tìm mốc km gần nhất, sử dụng Optional để xử lý an toàn
        return scheduleRepository.findClosestKilometerMark(kilometers)
                .map(closestMark -> {
                    // 2. Nếu tìm thấy, lấy tất cả hạng mục tại mốc km đó
                    return scheduleRepository.findByKilometerMark(closestMark).stream()
                            .map(schedule -> new MaintenanceScheduleDto(
                                    schedule.getItem().getName(),
                                    schedule.getItem().getCategory(),
                                    schedule.getAction())) // Action bây giờ là Enum
                            .collect(Collectors.toList());
                })
                // 3. Nếu không tìm thấy mốc nào, trả về danh sách rỗng
                .orElse(Collections.emptyList());
    }
}