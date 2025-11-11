package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.*;
import com.ecar.ecarservice.payload.responses.*;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.UserService;
import com.ecar.ecarservice.threads.EmailThread;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // ====================== LICH SU BAO DUONG ======================
    @Override
    public Page<MaintenanceHistoryDTO> getMaintenanceHistory(OidcUser oidcUser, MaintenanceHistorySearchRequest request) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        return maintenanceHistoryRepository.searchByOwner(
                        currentUser.getId(),
                        request.getSearchValue(),
                        pageRequest)
                .map(this::convertToDTO);
    }

    // ====================== DAT LICH BAO DUONG ======================
    @Override
    @Transactional
    public MaintenanceHistory createSchedule(MaintenanceScheduleRequest request, OidcUser oidcUser) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElseThrow();
        Center center = centerRepository.findById(request.centerId()).orElseThrow();
        LocalDateTime dateTime = LocalDateTime.of(request.scheduleDate(), request.scheduleTime());

        MaintenanceHistory history = new MaintenanceHistory();
        history.setVehicle(vehicle);
        history.setOwner(appUserRepository.getReferenceById(currentUser.getId()));
        history.setNumOfKm(request.numOfKm());
        history.setSubmittedAt(LocalDateTime.now());
        history.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);
        history.setIsMaintenance(request.isMaintenance());
        history.setIsRepair(request.isRepair());
        history.setRemark(request.remark());
        history.setCenter(center);
        history.setScheduleTime(request.scheduleTime());
        history.setScheduleDate(request.scheduleDate());

        // Gui mail xac nhan
        BookingRequest bookingRequest = new BookingRequest(
                currentUser.getFullName(),
                vehicle.getLicensePlate(),
                vehicle.getCarModel().getCarName(),
                center.getCenterName(),
                dateTime,
                currentUser.getEmail()
        );
        executorService.submit(new EmailThread(bookingRequest, emailService));

        return maintenanceHistoryRepository.save(history);
    }

    // ====================== QUAN LY PHIEU ======================
    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getTickets(OidcUser user) {
        return maintenanceHistoryRepository.findAllAndSortForManagement()
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

        Long carModelId = (history.getVehicle() != null && history.getVehicle().getCarModel() != null)
                ? history.getVehicle().getCarModel().getId() : null;
        String carName = (history.getVehicle() != null && history.getVehicle().getCarModel() != null)
                ? history.getVehicle().getCarModel().getCarName() : null;
        String licensePlate = (history.getVehicle() != null) ? history.getVehicle().getLicensePlate() : "N/A";

        return new MaintenanceTicketResponse(
                history.getId(),
                ownerFullName,
                carModelId,
                carName,
                licensePlate,
                history.getNumOfKm(),
                history.getSubmittedAt(),
                staffFullName,
                staffId,
                history.getStaffReceiveAt(),
                technicianFullName,
                technicianId,
                history.getTechnicianReceivedAt(),
                history.getCompletedAt(),
                history.getStatus(),
                history.getIsMaintenance(),
                history.getIsRepair(),
                history.getCenter().getCenterName(),
                history.getScheduleDate(),
                history.getScheduleTime(),
                history.getMaintenanceScheduleId()
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

    // ====================== MILESTONE ======================
    @Override
    public List<MilestoneResponse> getMilestone(Long carModelId) {
        return maintenanceMileStoneRepository.findALlByCarModelIdOrderByYearAt(carModelId)
                .stream()
                .map(m -> new MilestoneResponse(m.getId(), m.getKilometerAt(), m.getYearAt()))
                .toList();
    }

    // ====================== SERVICE GROUP ======================
    @Override
    public List<ServiceGroup> getMaintenanceServiceGroup(Long carModelId, Long maintenanceMilestoneId) {
        List<MaintenanceSchedule> schedules = maintenanceScheduleRepository
                .findAllScheduleByCarModelIdAndMilestoneId(carModelId, maintenanceMilestoneId);

        Map<String, List<ServiceItem>> rs = schedules.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getService().getCategory(),
                        Collectors.mapping(s -> new ServiceItem(
                                s.getId(),
                                s.getService().getServiceName(),
                                s.getIsDefault()
                        ), Collectors.toList())
                ));

        return rs.entrySet().stream()
                .map(e -> new ServiceGroup(
                        e.getKey(),
                        e.getValue().stream()
                                .sorted(Comparator.comparing(ServiceItem::serviceName))
                                .toList()
                ))
                .toList();
    }

    @Override
    public List<ServiceGroup> getServiceGroup(Long ticketId) {
        String SERVICE_TYPE_FIX = "F";
        List<Long> selectedIds = maintenanceItemRepository.findAllServiceIds(ticketId);
        Map<String, List<ServiceItem>> rs = serviceRepository.findAllByServiceType(SERVICE_TYPE_FIX)
                .stream()
                .collect(Collectors.groupingBy(
                        com.ecar.ecarservice.entities.Service::getCategory,
                        Collectors.mapping(s -> new ServiceItem(
                                s.getId(),
                                s.getServiceName(),
                                selectedIds.contains(s.getId())
                        ), Collectors.toList())
                ));

        return rs.entrySet().stream()
                .map(e -> new ServiceGroup(
                        e.getKey(),
                        e.getValue().stream()
                                .sorted(Comparator.comparing(ServiceItem::serviceName))
                                .toList()
                ))
                .sorted(Comparator.comparing(ServiceGroup::category))
                .toList();
    }

    // ====================== TAO SERVICE ======================
    @Override
    @Transactional
    public void createService(ServiceCreateRequest request, OidcUser oidcUser) {
        // Lấy thông tin staff hiện tại
        AppUser currentUser = userService.getCurrentUser(oidcUser);

        // Lấy thông tin MaintenanceHistory theo ticketId
        MaintenanceHistory maintenanceHistory = maintenanceHistoryRepository.findById(request.ticketId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Service ticket not found with ID: " + request.ticketId()));

        // Lấy thông tin kỹ thuật viên được giao
        AppUser assignedTechnician = appUserRepository.findById(request.technicianId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Technician not found with ID: " + request.technicianId()));

        // Cập nhật số km, staff và technician
        maintenanceHistory.setNumOfKm(request.numOfKm());
        maintenanceHistory.setStaff(currentUser);
        maintenanceHistory.setTechnician(assignedTechnician);
        maintenanceHistory.setStaffReceiveAt(LocalDateTime.now());
        maintenanceHistory.setTechnicianReceivedAt(LocalDateTime.now());

        // Cập nhật trạng thái đã được technician nhận
        maintenanceHistory.setStatus(MaintenanceStatus.TECHNICIAN_RECEIVED);

        // Lưu thông tin maintenanceHistory
        MaintenanceHistory saved = maintenanceHistoryRepository.save(maintenanceHistory);

        // ======================= LƯU CÁC SERVICE =======================
        List<MaintenanceItem> items = new ArrayList<>();

        // Lưu milestone
        MaintenanceItem milestone = new MaintenanceItem();
        milestone.setMaintenanceHistoryId(saved.getId());
        milestone.setMaintenanceMilestoneId(request.scheduleId());
        items.add(milestone);

        // Lưu các service được chọn
        for (Long id : request.checkedServiceIds()) {
            MaintenanceItem s = new MaintenanceItem();
            s.setMaintenanceHistoryId(saved.getId());
            s.setServiceId(id);
            items.add(s);
        }
        maintenanceItemRepository.saveAll(items);

        // ======================= GỬI MAIL THÔNG BÁO PHÂN CÔNG TECHNICIAN =======================
        emailService.sendTechnicianAssignedEmail(assignedTechnician, saved);

        // ======================= GỬI MAIL NGAY KHI TECHNICIAN NHẬN XE =======================
        AppUser owner = saved.getOwner();
        Vehicle vehicle = saved.getVehicle();
        if (owner != null && assignedTechnician != null && vehicle != null && vehicle.getCarModel() != null) {
            emailService.sendTechnicianReceivedEmail(owner, assignedTechnician, vehicle);
        } else {
            System.err.println("Cannot send technician received email: missing owner, technician, or vehicle data.");
        }
    }


    // ====================== LAY PHIEU KY THUAT VIEN ======================
    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getTicketsForTechnician(OidcUser user) {
        AppUser currentUser = userService.getCurrentUser(user);
        return maintenanceHistoryRepository.findByTechnicianIdOrderByStatusAscTechnicianReceivedAtDesc(currentUser.getId())
                .stream()
                .map(this::fromMaintenanceHistory)
                .toList();
    }

    // ====================== KY THUAT VIEN HOAN TAT CONG VIEC ======================
    @Override
    @Transactional
    public void completeServiceByTechnician(Long ticketId, OidcUser oidcUser) {
        AppUser currentTechnician = userService.getCurrentUser(oidcUser);
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Service ticket not found with id: " + ticketId));

        if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(currentTechnician.getId())) {
            throw new AccessDeniedException("You are not assigned to this service ticket.");
        }

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_RECEIVED) {
            throw new IllegalStateException("Ticket is not ready to be completed. Current state: " + ticket.getStatus());
        }

        ticket.setStatus(MaintenanceStatus.TECHNICIAN_COMPLETED);
        ticket.setCompletedAt(LocalDateTime.now());
        maintenanceHistoryRepository.save(ticket);
    }

    // ====================== CAP NHAT TRANG THAI & XE SAU BAO DUONG ======================
    @Override
    @Transactional
    public MaintenanceHistoryDTO completeTechnicianTask(Long id) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service ticket not found with id: " + id));

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_RECEIVED) {
            throw new IllegalStateException("Ticket is not ready to be completed. Current state: " + ticket.getStatus());
        }

        ticket.setStatus(MaintenanceStatus.TECHNICIAN_COMPLETED);
        ticket.setCompletedAt(LocalDateTime.now());
        MaintenanceHistory savedTicket = maintenanceHistoryRepository.save(ticket);

        Vehicle vehicle = savedTicket.getVehicle();
        if (vehicle != null) {
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
}
