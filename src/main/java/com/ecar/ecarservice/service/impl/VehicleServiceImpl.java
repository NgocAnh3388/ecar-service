package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Vehicle;
import com.ecar.ecarservice.repositories.VehicleRepository;
import com.ecar.ecarservice.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getMyVehicles(AppUser currentUser) {
        return vehicleRepository.findByOwnerIdAndActiveTrue(currentUser.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleDto addVehicle(VehicleDto vehicleDto, AppUser currentUser) {
        Vehicle vehicle = new Vehicle();
        vehicle.setOwner(currentUser);
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setCarModel(vehicleDto.getCarModel());
        vehicle.setVinNumber(vehicleDto.getVinNumber());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return convertToDto(savedVehicle);
    }

    @Override
    @Transactional
    public VehicleDto updateVehicle(Long vehicleId, VehicleDto vehicleDto, AppUser currentUser) {
        Vehicle vehicle = findVehicleByIdAndOwner(vehicleId, currentUser);

        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setCarModel(vehicleDto.getCarModel());
        vehicle.setVinNumber(vehicleDto.getVinNumber());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToDto(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId, AppUser currentUser) {
        Vehicle vehicle = findVehicleByIdAndOwner(vehicleId, currentUser);

        vehicle.setActive(false);

        vehicleRepository.save(vehicle);
    }

    private Vehicle findVehicleByIdAndOwner(Long vehicleId, AppUser owner) {
        return vehicleRepository.findByIdAndOwnerIdAndActiveTrue(vehicleId, owner.getId())
                .orElseThrow(() -> new EntityNotFoundException("Active vehicle not found with id: " + vehicleId + " for current user."));
    }

    private VehicleDto convertToDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCarModel(vehicle.getCarModel());
        dto.setVinNumber(vehicle.getVinNumber());
        dto.setActive(vehicle.isActive());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setCreatedBy(vehicle.getCreatedBy());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        dto.setUpdatedBy(vehicle.getUpdatedBy());
        return dto;
    }
}