package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import com.ecar.ecarservice.payload.responses.ServiceItem;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceHistoryRepository maintenanceHistoryRepository;
    private final UserService userService;
    private final VehicleRepository vehicleRepository;
    private final AppUserRepository appUserRepository;
    private final CenterRepository centerRepository;
    private final MaintenanceMileStoneRepository maintenanceMileStoneRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final ServiceRepository serviceRepository;
    private final MaintenanceItemRepository maintenanceItemRepository;

    public MaintenanceServiceImpl(MaintenanceHistoryRepository maintenanceHistoryRepository,
                                  UserService userService,
                                  VehicleRepository vehicleRepository,
                                  AppUserRepository appUserRepository,
                                  CenterRepository centerRepository,
                                  MaintenanceMileStoneRepository maintenanceMileStoneRepository,
                                  MaintenanceScheduleRepository maintenanceScheduleRepository,
                                  ServiceRepository serviceRepository,
                                  MaintenanceItemRepository maintenanceItemRepository) {
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
        this.userService = userService;
        this.vehicleRepository = vehicleRepository;
        this.appUserRepository = appUserRepository;
        this.centerRepository = centerRepository;
        this.maintenanceMileStoneRepository = maintenanceMileStoneRepository;
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.serviceRepository = serviceRepository;
        this.maintenanceItemRepository = maintenanceItemRepository;
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
        history.setOwner(this.appUserRepository.getReferenceById(currentUser.getId()));
        history.setNumOfKm(request.numOfKm());
        history.setSubmittedAt(LocalDateTime.now());
        history.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);
        history.setIsMaintenance(request.isMaintenance());
        history.setIsRepair(request.isRepair());
        history.setRemark(request.remark());
        history.setCenter(this.centerRepository.getReferenceById(request.centerId()));
        history.setScheduleTime(request.scheduleTime());
        history.setScheduleDate(request.scheduleDate());

        //TODO: gửi mail xác nhận đã nhận được yêu cầu

        this.maintenanceHistoryRepository.save(history);
    }

    @Override
    public List<MaintenanceTicketResponse> getTickets(OidcUser user) {
//        LocalDate today = LocalDate.now();
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end   = today.plusDays(1).atStartOfDay();
        return this.maintenanceHistoryRepository.findAllWithinToday()
                .stream()
                .map(this::fromMaintenanceHistory)
                .toList();
    }

    private MaintenanceTicketResponse fromMaintenanceHistory(MaintenanceHistory history) {
        return new MaintenanceTicketResponse(
                history.getId(),
                history.getOwner().getFullName(),
                history.getVehicle().getId(),
                history.getVehicle().getCarModel().getCarName(),
                history.getVehicle().getLicensePlate(),
                history.getNumOfKm(),
                history.getSubmittedAt(),
                history.getStaff() == null ? null : history.getStaff().getFullName(),
                history.getStaff() == null ? null : history.getStaff().getId(),
                history.getStaffReceiveAt(),
                history.getTechnician() == null ? null :history.getTechnician().getFullName(),
                history.getTechnician() == null ? null : history.getTechnician().getId(),
                history.getTechnicianReceivedAt(),
                history.getCompletedAt(),
                history.getStatus(),
                history.getIsMaintenance(),
                history.getIsRepair(),
                history.getCenter().getCenterName(),
                history.getScheduleDate(),
                history.getScheduleTime()
        );
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

    @Override
    public List<MilestoneResponse> getMilestone(Long carModelId) {
        return this.maintenanceMileStoneRepository.findALlByCarModelIdOrderByYearAt(carModelId)
                .stream()
                .map(this::fromMaintenanceMilestone)
                .toList();
    }

    private MilestoneResponse fromMaintenanceMilestone(MaintenanceMileStone maintenanceMileStone) {
        return new MilestoneResponse(
                maintenanceMileStone.getId(),
                maintenanceMileStone.getKilometerAt(),
                maintenanceMileStone.getYearAt()
        );
    }

    @Override
    public List<ServiceGroup> getMaintenanceServiceGroup(Long carModelId, Long maintenanceMilestoneId) {
        List<MaintenanceSchedule> schedules = this.maintenanceScheduleRepository
                .findAllScheduleByCarModelIdAndMilestoneId(
                        carModelId,
                        maintenanceMilestoneId
                );

        Map<String, List<ServiceItem>> rs = schedules
                .stream()
                .collect(Collectors
                        .groupingBy(schedule -> schedule.getService().getCategory(),
                                Collectors.mapping(schedule ->
                                        new ServiceItem(
                                                schedule.getId(),
                                                schedule.getService().getServiceName(),
                                                schedule.getIsDefault()
                                        ), Collectors.toList()
                                )));
        return rs.entrySet()
                .stream()
                .map(entry -> {
                    List<ServiceItem> sortedItems = entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(ServiceItem::serviceName))
                            .toList();
                    return new ServiceGroup(entry.getKey(), sortedItems);
                })
                .toList();
    }

    @Override
    public List<ServiceGroup> getServiceGroup(Long ticketId) {
        String SERVICE_TYPE_FIX = "F";
        List<Long> selectedIds = this.maintenanceItemRepository.findAllServiceIds(ticketId);
        Map<String, List<ServiceItem>> rs = this.serviceRepository.findAllByServiceType(SERVICE_TYPE_FIX)
                .stream()
                .collect(Collectors
                        .groupingBy(com.ecar.ecarservice.entities.Service::getCategory,
                                Collectors.mapping(s ->
                                        new ServiceItem(
                                                s.getId(),
                                                s.getServiceName(),
                                                selectedIds.contains(s.getId())
                                        ), Collectors.toList()
                                )));
        return rs.entrySet()
                .stream()
                .map(entry -> {
                    List<ServiceItem> sortedItems = entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(ServiceItem::serviceName))
                            .toList();
                    return new ServiceGroup(entry.getKey(), sortedItems);
                })
                .toList().stream().sorted(Comparator.comparing(ServiceGroup::category)).toList();
    }

    @Override
    public void createService(ServiceCreateRequest request, OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
        MaintenanceHistory maintenanceHistory = this.maintenanceHistoryRepository.findById(request.ticketId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + request.ticketId()));
        AppUser assignedTechnician = this.appUserRepository.findById(request.technicianId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kỹ thuật viên với ID: " + request.technicianId()));
        maintenanceHistory.setNumOfKm(request.numOfKm());

        maintenanceHistory.setStaff(currentUser);
        maintenanceHistory.setTechnician(assignedTechnician);
        maintenanceHistory.setStaffReceiveAt(LocalDateTime.now()); // Thời điểm nhân viên tiếp nhận
        maintenanceHistory.setTechnicianReceivedAt(LocalDateTime.now()); // Thời điểm KTV nhận việc
        maintenanceHistory.setStatus(MaintenanceStatus.TECHNICIAN_RECEIVED);
        MaintenanceHistory savedMH = this.maintenanceHistoryRepository.save(maintenanceHistory);

        List<MaintenanceItem> items = new ArrayList<>();
        MaintenanceItem milestone = new MaintenanceItem();
        milestone.setMaintenanceHistoryId(savedMH.getId());
        milestone.setMaintenanceMilestoneId(request.scheduleId());
        items.add(milestone);

        for (Long i : request.checkedServiceIds()) {
            MaintenanceItem service = new MaintenanceItem();
            service.setMaintenanceHistoryId(savedMH.getId());
            service.setServiceId(i);
            items.add(service);
        }

        this.maintenanceItemRepository.saveAll(items);
    }


    @Override
    public List<MaintenanceTicketResponse> getTicketsForTechnician(OidcUser user) {
        AppUser currentUser = userService.getCurrentUser(user);

        return this.maintenanceHistoryRepository.findByTechnicianIdOrderByStatus(currentUser.getId())
                .stream()
                .map(this::fromMaintenanceHistory)
                .toList();
    }

    @Override
    @Transactional // <-- THÊM ANNOTATION NÀY VÀO
    public MaintenanceHistoryDTO completeTechnicianTask(Long maintenanceId) {
        // 1. Tìm bản ghi bảo dưỡng trong DB bằng ID
        MaintenanceHistory maintenance = maintenanceHistoryRepository.findById(maintenanceId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + maintenanceId));

        // 2. Cập nhật trạng thái và thời gian hoàn thành
        maintenance.setStatus(MaintenanceStatus.TECHNICIAN_COMPLETED);
        maintenance.setCompletedAt(LocalDateTime.now());

        // 3. Lưu lại thay đổi vào DB
        // Do có @Transactional, bạn không cần gọi save() một cách tường minh nữa,
        // Spring sẽ tự động lưu khi transaction kết thúc. Nhưng để code rõ ràng, bạn có thể giữ lại.
        MaintenanceHistory updatedMaintenance = maintenanceHistoryRepository.save(maintenance);

        // 4. Map entity đã cập nhật sang DTO và trả về
        // Vì transaction vẫn còn mở ở đây, việc truy cập vehicle.getCarModel() sẽ thành công.
        return convertToDTO(updatedMaintenance);
    }


}