package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import com.ecar.ecarservice.enitiies.CarModel;
import com.ecar.ecarservice.repositories.CarModelRepository;
import com.ecar.ecarservice.repositories.MaintenanceScheduleRepository;
import com.ecar.ecarservice.service.MaintenanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceScheduleRepository scheduleRepository;
    private final CarModelRepository carModelRepository;

    public MaintenanceServiceImpl(MaintenanceScheduleRepository scheduleRepository, CarModelRepository carModelRepository) {
        this.scheduleRepository = scheduleRepository;
        this.carModelRepository = carModelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceScheduleDto> getScheduleByKilometers(String carModelName, int kilometers) {
        CarModel carModel = carModelRepository.findByNameIgnoreCase(carModelName)
                .orElseThrow(() -> new EntityNotFoundException("Car model not found: " + carModelName));

        return scheduleRepository.findClosestKilometerMark(carModel.getId(), kilometers)
                .map(closestMark -> {
                    return scheduleRepository.findByCarModelIdAndKilometerMark(carModel.getId(), closestMark).stream()
                            .map(schedule -> new MaintenanceScheduleDto(
                                    schedule.getItem().getName(),
                                    schedule.getItem().getCategory(),
                                    schedule.getAction(),
                                    schedule.getPrice()))
                            .collect(Collectors.toList());
                })
                .orElse(Collections.emptyList());
    }
}