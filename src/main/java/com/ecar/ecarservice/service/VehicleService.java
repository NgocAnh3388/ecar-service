package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.enitiies.AppUser;

import java.util.List;

public interface VehicleService {
    List<VehicleDto> getMyVehicles(AppUser currentUser);
    VehicleDto addVehicle(VehicleDto vehicleDto, AppUser currentUser);
    VehicleDto updateVehicle(Long vehicleId, VehicleDto vehicleDto, AppUser currentUser);
    void deleteVehicle(Long vehicleId, AppUser currentUser);
}