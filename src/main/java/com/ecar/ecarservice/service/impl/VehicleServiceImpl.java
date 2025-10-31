package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Vehicle;
import com.ecar.ecarservice.payload.requests.VehicleRequest;
import com.ecar.ecarservice.payload.responses.CarModelResponse;
import com.ecar.ecarservice.payload.responses.VehicleResponse;
import com.ecar.ecarservice.repositories.CarModelRepository;
import com.ecar.ecarservice.repositories.VehicleRepository;
import com.ecar.ecarservice.service.CarModelService;
import com.ecar.ecarservice.service.UserService;
import com.ecar.ecarservice.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;;
    private final UserService userService;
    private final CarModelRepository carModelRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserService userService,
                              CarModelRepository carModelRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userService = userService;
        this.carModelRepository = carModelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getMyVehicles(OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
        return vehicleRepository.findByOwnerIdAndActiveTrue(currentUser.getId())
                .stream()
                .map(this::fromVehicle)
                .toList();
    }

    private VehicleResponse fromVehicle(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                new CarModelResponse(
                        vehicle.getCarModel().getId(),
                        vehicle.getCarModel().getCarName(),
                        vehicle.getCarModel().getCarType()
                ),
                vehicle.getVinNumber(),
                vehicle.getNextKm(),
                vehicle.getNextDate(),
                vehicle.getOldKm(),
                vehicle.getOldDate()
        );
    }

    @Override
    @Transactional
    public void addVehicle(VehicleRequest request, OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
        Vehicle vehicle = new Vehicle();
        vehicle.setOwnerId(currentUser.getId());
        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setCarModel(this.carModelRepository.getReferenceById(request.carModelId()));
        vehicle.setVinNumber(request.vinNumber());
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public VehicleDto updateVehicle(Long vehicleId, VehicleDto vehicleDto, OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
        Vehicle vehicle = findVehicleByIdAndOwner(vehicleId, currentUser);

        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setCarModel(vehicleDto.getCarModel());
        vehicle.setVinNumber(vehicleDto.getVinNumber());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToDto(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId, OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
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
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCarModel(vehicle.getCarModel());
        dto.setVinNumber(vehicle.getVinNumber());
        dto.setNextKm(vehicle.getNextKm());
        dto.setNextDate(vehicle.getNextDate());
        dto.setOldDate(vehicle.getOldDate());
        dto.setOldKm(vehicle.getOldKm());
        return dto;
    }

    //    @Override
//    public List<VehicleDashboardInfoDTO> getVehicleDashboardInfo(OidcUser oidcUser) {
//        AppUser currentUser = this.getCurrentUser(oidcUser);
//        StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("get_customer_dashboard_info");
//        query.registerStoredProcedureParameter(1, Class.class, ParameterMode.REF_CURSOR);
//        query.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);
//        query.setParameter(2, currentUser.getId());
//        query.execute();
//        List<Object> result = (List<Object>) query.getResultList();
//        List<VehicleDashboardInfoDTO> rs = new ArrayList<>();
//        for (Object o : result) {
//            Object[] obj = getObjects((Object[]) o);
//            Long nextKm = Long.parseLong(String.valueOf(obj[6] == null ? "0" : obj[6]));
//            rs.add(new VehicleDashboardInfoDTO(productName, productType, productGender, price, quantity, accumulation));
//        }
//        return List.of();
//    }
//
//    private Object[] getObjects(Object[] o) {
//        String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSSSS";
//        Object[] obj = o;
//        String carName = String.valueOf(obj[0]);
//        String carType = String.valueOf(obj[1]);
//        String licensePlate = String.valueOf(obj[2]);
//        String vinNumber = String.valueOf(obj[3]);
//        LocalDateTime submittedAt = Optional.ofNullable(obj[3])
//                .map(Object::toString)
//                .map(s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern(DATE_PATTERN)))
//                .orElse(null);
//        LocalDateTime completedAt = Optional.ofNullable(obj[4])
//                .map(Object::toString)
//                .map(s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern(DATE_PATTERN)))
//                .orElse(null);
//        Long numOfKm = Long.parseLong(String.valueOf(obj[4] == null ? "0" : obj[4]));
//        Long currentKm = Long.parseLong(String.valueOf(obj[5] == null ? "0" : obj[5]));
//        Long nextYear = Long.parseLong(String.valueOf(obj[6] == null ? "0" : obj[6]));
//        return obj;
//    }
}
