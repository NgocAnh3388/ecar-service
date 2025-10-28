package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import java.util.List;

public interface MaintenanceService {
    List<MaintenanceScheduleDto> getScheduleByKilometers(int kilometers);
}