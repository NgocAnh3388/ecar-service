package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.MaintenanceHistorySearchRequest;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.payload.responses.MilestoneResponse;
import com.ecar.ecarservice.payload.responses.ServiceGroup;
import com.ecar.ecarservice.payload.responses.ServiceItem;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService;

    public MaintenanceServiceImpl(MaintenanceHistoryRepository maintenanceHistoryRepository,
                                  UserService userService,
                                  VehicleRepository vehicleRepository,
                                  AppUserRepository appUserRepository,
                                  CenterRepository centerRepository,
                                  MaintenanceMileStoneRepository maintenanceMileStoneRepository,
                                  MaintenanceScheduleRepository maintenanceScheduleRepository,
                                  ServiceRepository serviceRepository,
                                  MaintenanceItemRepository maintenanceItemRepository,
                                  EmailService emailService) {
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
        this.userService = userService;
        this.vehicleRepository = vehicleRepository;
        this.appUserRepository = appUserRepository;
        this.centerRepository = centerRepository;
        this.maintenanceMileStoneRepository = maintenanceMileStoneRepository;
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.serviceRepository = serviceRepository;
        this.maintenanceItemRepository = maintenanceItemRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public MaintenanceHistory createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);
        MaintenanceHistory history = new MaintenanceHistory();
        history.setVehicle(this.vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + request.vehicleId())));
        history.setOwner(currentUser);
        history.setNumOfKm(request.numOfKm());
        history.setSubmittedAt(LocalDateTime.now());
        history.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);
        history.setIsMaintenance(request.isMaintenance());
        history.setIsRepair(request.isRepair());
        history.setRemark(request.remark());
        history.setCenter(this.centerRepository.findById(request.centerId())
                .orElseThrow(() -> new EntityNotFoundException("Center not found with ID: " + request.centerId())));
        history.setScheduleTime(request.scheduleTime());
        history.setScheduleDate(request.scheduleDate());

        MaintenanceHistory savedHistory = this.maintenanceHistoryRepository.save(history);

        if (savedHistory != null) {
            // GHI CHÚ: Dòng này bây giờ đã hợp lệ và không còn báo lỗi.
            emailService.sendScheduleConfirmationEmail(savedHistory);
        }

        return savedHistory;
    }

    @Override
    @Transactional
    public void assignTaskToTechnician(ServiceCreateRequest request, OidcUser oidcUser) {
        AppUser currentUser = this.userService.getCurrentUser(oidcUser);
        MaintenanceHistory maintenanceHistory = this.maintenanceHistoryRepository.findById(request.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Service ticket not found with ID: " + request.ticketId()));
        AppUser assignedTechnician = this.appUserRepository.findById(request.technicianId())
                .orElseThrow(() -> new EntityNotFoundException("Technician not found with ID: " + request.technicianId()));

        maintenanceHistory.setNumOfKm(request.numOfKm());
        maintenanceHistory.setStaff(currentUser);
        maintenanceHistory.setTechnician(assignedTechnician);
        maintenanceHistory.setStaffReceiveAt(LocalDateTime.now());
        maintenanceHistory.setTechnicianReceivedAt(LocalDateTime.now());
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
    @Transactional
    public MaintenanceHistoryDTO completeTechnicianTask(Long ticketId, OidcUser oidcUser) {
        AppUser currentTechnician = userService.getCurrentUser(oidcUser);
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentTechnician.getId())) {
            throw new AccessDeniedException("You are not assigned to this service ticket.");
        }

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_RECEIVED) {
            throw new IllegalStateException("Ticket cannot be completed. Current state: " + ticket.getStatus());
        }

        ticket.setStatus(MaintenanceStatus.TECHNICIAN_COMPLETED);
        ticket.setCompletedAt(LocalDateTime.now());
        MaintenanceHistory savedTicket = maintenanceHistoryRepository.save(ticket);

        Vehicle vehicle = savedTicket.getVehicle();
        if (vehicle != null && savedTicket.getNumOfKm() != null) {
            long nextKm = (savedTicket.getNumOfKm() / 12000 + 1) * 12000;
            LocalDateTime nextDate = savedTicket.getCompletedAt().plusYears(1);

            vehicle.setOldKm(savedTicket.getNumOfKm());
            vehicle.setOldDate(savedTicket.getCompletedAt());
            vehicle.setNextKm(nextKm);
            vehicle.setNextDate(nextDate);
            vehicleRepository.save(vehicle);
        }

        return convertToDTO(savedTicket);
    }

    @Override
    @Transactional
    public void finalizeAndHandOver(Long ticketId) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_COMPLETED) {
            throw new IllegalStateException("Ticket must be completed by technician before finalizing. Current state: " + ticket.getStatus());
        }

        ticket.setStatus(MaintenanceStatus.DONE);
        ticket.setHandOverAt(LocalDateTime.now());
        maintenanceHistoryRepository.save(ticket);
    }

    // GHI CHÚ: Các phương thức GET và helper khác không thay đổi.
    @Override
    public Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        return this.maintenanceHistoryRepository.searchByOwner(currentUser.getId(), request.getSearchValue(), pageRequest)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getTickets(OidcUser user) {
        return this.maintenanceHistoryRepository.findAllAndSortForManagement()
                .stream()
                .map(this::fromMaintenanceHistory)
                .toList();
    }

    @Override
    public List<MilestoneResponse> getMilestone(Long carModelId) {
        return this.maintenanceMileStoneRepository.findALlByCarModelIdOrderByYearAt(carModelId)
                .stream()
                .map(this::fromMaintenanceMilestone)
                .toList();
    }

    @Override
    public List<ServiceGroup> getMaintenanceServiceGroup(Long carModelId, Long maintenanceMilestoneId) {
        List<MaintenanceSchedule> schedules = this.maintenanceScheduleRepository
                .findAllScheduleByCarModelIdAndMilestoneId(carModelId, maintenanceMilestoneId);

        Map<String, List<ServiceItem>> rs = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getService().getCategory(),
                        Collectors.mapping(schedule -> new ServiceItem(schedule.getId(), schedule.getService().getServiceName(), schedule.getIsDefault()),
                                Collectors.toList())));
        return rs.entrySet().stream()
                .map(entry -> {
                    List<ServiceItem> sortedItems = entry.getValue().stream().sorted(Comparator.comparing(ServiceItem::serviceName)).toList();
                    return new ServiceGroup(entry.getKey(), sortedItems);
                })
                .toList();
    }

    @Override
    public List<ServiceGroup> getServiceGroup(Long ticketId) {
        String SERVICE_TYPE_FIX = "F";
        List<Long> selectedIds = this.maintenanceItemRepository.findAllServiceIds(ticketId);
        Map<String, List<ServiceItem>> rs = this.serviceRepository.findAllByServiceType(SERVICE_TYPE_FIX).stream()
                .collect(Collectors.groupingBy(com.ecar.ecarservice.entities.Service::getCategory,
                        Collectors.mapping(s -> new ServiceItem(s.getId(), s.getServiceName(), selectedIds.contains(s.getId())),
                                Collectors.toList())));
        return rs.entrySet().stream()
                .map(entry -> {
                    List<ServiceItem> sortedItems = entry.getValue().stream().sorted(Comparator.comparing(ServiceItem::serviceName)).toList();
                    return new ServiceGroup(entry.getKey(), sortedItems);
                })
                .sorted(Comparator.comparing(ServiceGroup::category))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getTicketsForTechnician(OidcUser user) {
        AppUser currentUser = userService.getCurrentUser(user);
        return this.maintenanceHistoryRepository.findByTechnicianIdOrderByStatusAscTechnicianReceivedAtDesc(currentUser.getId())
                .stream()
                .map(this::fromMaintenanceHistory)
                .toList();
    }

    private MaintenanceTicketResponse fromMaintenanceHistory(MaintenanceHistory history) {
        String ownerFullName = (history.getOwner() != null) ? history.getOwner().getFullName() : null;
        String staffFullName = (history.getStaff() != null) ? history.getStaff().getFullName() : null;
        Long staffId = (history.getStaff() != null) ? history.getStaff().getId() : null;
        String technicianFullName = (history.getTechnician() != null) ? history.getTechnician().getFullName() : null;
        Long technicianId = (history.getTechnician() != null) ? history.getTechnician().getId() : null;
        Long carModelId = (history.getVehicle() != null && history.getVehicle().getCarModel() != null) ? history.getVehicle().getCarModel().getId() : null;
        String carName = (history.getVehicle() != null && history.getVehicle().getCarModel() != null) ? history.getVehicle().getCarModel().getCarName() : null;
        String licensePlate = (history.getVehicle() != null) ? history.getVehicle().getLicensePlate() : "N/A";

        return new MaintenanceTicketResponse(history.getId(), ownerFullName, carModelId, carName, licensePlate, history.getNumOfKm(), history.getSubmittedAt(), staffFullName, staffId, history.getStaffReceiveAt(), technicianFullName, technicianId, history.getTechnicianReceivedAt(), history.getCompletedAt(), history.getStatus(), history.getIsMaintenance(), history.getIsRepair(), history.getCenter().getCenterName(), history.getScheduleDate(), history.getScheduleTime());
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

    private MilestoneResponse fromMaintenanceMilestone(MaintenanceMileStone maintenanceMileStone) {
        return new MilestoneResponse(maintenanceMileStone.getId(), maintenanceMileStone.getKilometerAt(), maintenanceMileStone.getYearAt());
    }
}