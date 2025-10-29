package com.ecar.ecarservice.service.impl;


import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.MaintenanceHistory;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import com.ecar.ecarservice.repositories.VehicleRepository;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceHistoryRepository maintenanceHistoryRepository;
    private final UserService userService;
    private final VehicleRepository vehicleRepository;


    public MaintenanceServiceImpl(MaintenanceHistoryRepository maintenanceHistoryRepository,
                UserService userService,
                VehicleRepository vehicleRepository) {
            this.maintenanceHistoryRepository = maintenanceHistoryRepository;
            this.userService = userService;
            this.vehicleRepository = vehicleRepository;
        }

        @Override

            public Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request) {
                AppUser currentUser = userService.getCurrentUser(oidcUser);
                PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
                return this.maintenanceHistoryRepository.search(
                                currentUser.getId(),
                                request.getSearchValue(),
                                pageRequest)
                        .map(this::convertToDTO);
            }

            @Override
            public void createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser) {
                AppUser currentUser = userService.getCurrentUser(oidcUser);

                MaintenanceHistory history = new MaintenanceHistory();
                history.setVehicle(this.vehicleRepository.getReferenceById(request.vehicleId()));
                history.setOwner_id(currentUser.getId());
                history.setNumOfKm(request.numOfKm());
                history.setSubmittedAt(LocalDateTime.now());
                history.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED.name());
                history.setIsMaintenance(request.isMaintenance());
                history.setIsRepair(request.isRepair());
                history.setRemark(request.remark());
                history.setCenterId(request.centerId());
                history.setScheduleTime(request.scheduleTime());
                history.setScheduleDate(request.scheduleDate());

                //TODO: gửi mail xác nhận đã nhận được yêu cầu

                this.maintenanceHistoryRepository.save(history);
            }

            private MaintenanceHistoryDTO convertToDTO(MaintenanceHistory maintenanceHistory) {
                return MaintenanceHistoryDTO.builder()
                        .carType(maintenanceHistory.getVehicle().getCarModel().getCarType())
                        .carName(maintenanceHistory.getVehicle().getCarModel().getCarName())
                        .licensePlate(maintenanceHistory.getVehicle().getLicensePlate())
                        .submittedAt(maintenanceHistory.getSubmittedAt())
                        .completedAt(maintenanceHistory.getCompletedAt())
                        .status(maintenanceHistory.getStatus())
                        .build();
            }
        }