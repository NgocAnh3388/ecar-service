package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.AdditionalCostRequest;
import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.dto.UsedPartDto;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.*;
import com.ecar.ecarservice.payload.responses.*;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.NotificationService;
import com.ecar.ecarservice.service.UserService;
import com.ecar.ecarservice.threads.EmailThread;
import com.ecar.ecarservice.threads.TechnicianEmailThread;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final MaintenanceItemPartRepository maintenanceItemPartRepository;
    private final SparePartRepository sparePartRepository;
    private final InventoryRepository inventoryRepository;

    // Inject Notification Service
    private final NotificationService notificationService;

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

        // --- THONG BAO CHO KHACH HANG ---
        notificationService.createNotification(currentUser,
                "Booking Confirmed",
                "Your appointment for " + vehicle.getLicensePlate() + " has been submitted successfully.",
                "BOOKING");

        return maintenanceHistoryRepository.save(history);
    }

    // ====================== QUAN LY PHIEU ======================
    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceTicketResponse> getTickets(OidcUser oidcUser) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);
        List<MaintenanceHistory> tickets;

        if (currentUser.getRoles().contains(AppRole.ADMIN)) {
            tickets = maintenanceHistoryRepository.findAllAndSortForManagement();
        } else {
            Center userCenter = currentUser.getCenter();
            if (userCenter == null) {
                return Collections.emptyList();
            }
            tickets = maintenanceHistoryRepository.findAllByCenterIdSortedForManagement(userCenter.getId());
        }

        return tickets.stream()
                .map(this::fromMaintenanceHistory)
                .collect(Collectors.toList());
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
                history.getCenter() != null ? history.getCenter().getCenterName() : null,
                history.getScheduleDate(),
                history.getScheduleTime(),
                history.getMaintenanceScheduleId(),
                history.getIsMaintenance(),
                history.getIsRepair()
        );
    }


    private MaintenanceHistoryDTO convertToDTO(MaintenanceHistory maintenanceHistory) {
        MaintenanceHistoryDTO.MaintenanceHistoryDTOBuilder builder = MaintenanceHistoryDTO.builder()
                .id(maintenanceHistory.getId())
                .carType(maintenanceHistory.getVehicle().getCarModel().getCarType())
                .carName(maintenanceHistory.getVehicle().getCarModel().getCarName())
                .licensePlate(maintenanceHistory.getVehicle().getLicensePlate())
                .submittedAt(maintenanceHistory.getSubmittedAt())
                .completedAt(maintenanceHistory.getCompletedAt())
                .status(maintenanceHistory.getStatus());
        return builder.build();
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

    // ====================== TAO SERVICE (GÁN TECHNICIAN) ======================
    @Override
    @Transactional
    public void createService(ServiceCreateRequest request, OidcUser oidcUser) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);

        MaintenanceHistory maintenanceHistory = maintenanceHistoryRepository.findById(request.ticketId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Service ticket not found with ID: " + request.ticketId()));

        AppUser assignedTechnician = appUserRepository.findById(request.technicianId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Technician not found with ID: " + request.technicianId()));

        long currentTasks = maintenanceHistoryRepository.countTasksByTechnicianAndDate(
                assignedTechnician.getId(),
                maintenanceHistory.getScheduleDate()
        );

        if (currentTasks >= 3) {
            throw new IllegalStateException("This technician has reached the daily limit of 3 tasks!");
        }

        maintenanceHistory.setNumOfKm(request.numOfKm());
        maintenanceHistory.setStaff(currentUser);
        maintenanceHistory.setTechnician(assignedTechnician);
        maintenanceHistory.setStaffReceiveAt(LocalDateTime.now());
        maintenanceHistory.setTechnicianReceivedAt(LocalDateTime.now());
        maintenanceHistory.setMaintenanceScheduleId(request.scheduleId());
        maintenanceHistory.setStatus(MaintenanceStatus.TECHNICIAN_RECEIVED);

        MaintenanceHistory saved = maintenanceHistoryRepository.save(maintenanceHistory);

        // Lưu các service được chọn
        List<MaintenanceItem> items = new ArrayList<>();
        MaintenanceItem milestone = new MaintenanceItem();
        milestone.setMaintenanceHistoryId(saved.getId());
        milestone.setMaintenanceMilestoneId(request.scheduleId());
        items.add(milestone);

        for (Long id : request.checkedServiceIds()) {
            MaintenanceItem s = new MaintenanceItem();
            s.setMaintenanceHistoryId(saved.getId());
            s.setServiceId(id);
            items.add(s);
        }
        maintenanceItemRepository.saveAll(items);

        emailService.sendTechnicianAssignedEmail(assignedTechnician, saved);

        // --- THONG BAO CHO TECHNICIAN ---
        notificationService.createNotification(assignedTechnician,
                "New Task Assigned",
                "You have been assigned a new task for vehicle " + saved.getVehicle().getLicensePlate(),
                "TASK");

        AppUser owner = saved.getOwner();
        Vehicle vehicle = saved.getVehicle();
        if (owner != null && assignedTechnician != null && vehicle != null && vehicle.getCarModel() != null) {
            executorService.submit(new TechnicianEmailThread(emailService, owner, assignedTechnician, vehicle));
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

    @Override
    @Transactional
    public MaintenanceHistoryDTO completeServiceByTechnician(Long ticketId, OidcUser oidcUser) {
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
        MaintenanceHistory savedTicket = maintenanceHistoryRepository.save(ticket);

        updateVehicleNextMaintenance(savedTicket);

        // Gửi email
        AppUser owner = savedTicket.getOwner();
        Vehicle vehicle = savedTicket.getVehicle();
        if (owner != null && currentTechnician != null && vehicle != null) {
            executorService.submit(() ->
                    emailService.sendTechnicianCompletedEmail(owner, currentTechnician, savedTicket)
            );
        }

        // --- THONG BAO CHO STAFF ---
        if (savedTicket.getStaff() != null) {
            notificationService.createNotification(savedTicket.getStaff(),
                    "Task Completed",
                    "Technician " + currentTechnician.getFullName() + " has completed task for " + vehicle.getLicensePlate(),
                    "TASK");
        }

        return convertToDTO(savedTicket);
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

        updateVehicleNextMaintenance(savedTicket);

        // --- THONG BAO CHO STAFF ---
        if (savedTicket.getStaff() != null) {
            notificationService.createNotification(savedTicket.getStaff(),
                    "Task Completed",
                    "Technician has completed task #" + id,
                    "TASK");
        }

        return convertToDTO(savedTicket);
    }

    private void updateVehicleNextMaintenance(MaintenanceHistory savedTicket) {
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
    }

    // ====================== HỦY PHIẾU ======================
    @Override
    @Transactional
    public void cancelMaintenance(Long id) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + id));

        if (ticket.getStatus() == MaintenanceStatus.DONE
                || ticket.getStatus() == MaintenanceStatus.TECHNICIAN_COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed order.");
        }

        ticket.setIsMaintenance(false);
        ticket.setRemark("Cancelled by staff/user");
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(MaintenanceStatus.CANCELLED);

        maintenanceHistoryRepository.save(ticket);

        if (ticket.getOwner() != null) {
            emailService.sendOrderCancelledEmail(ticket.getOwner(), ticket, "Cancelled by Staff request");

            // --- THONG BAO HUY DON ---
            notificationService.createNotification(ticket.getOwner(),
                    "Order Cancelled",
                    "Your maintenance order for " + ticket.getVehicle().getLicensePlate() + " has been cancelled.",
                    "CANCEL");
        }
    }


    // ====================== KÍCH HOẠT LẠI PHIẾU (REOPEN) ======================
    @Override
    @Transactional
    public void reopenMaintenance(Long id) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + id));

        if (Boolean.TRUE.equals(ticket.getIsMaintenance())) {
            throw new IllegalStateException("Only cancelled tickets can be reopened.");
        }

        ticket.setIsMaintenance(true);
        ticket.setRemark("Reopened by staff/admin");
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);

        maintenanceHistoryRepository.save(ticket);
    }

    // ====================== NHAC BAO DUONG TRUOC 10 NGAY ======================
    @Transactional
    @Scheduled(cron = "0 0 8 * * *")
    public void sendUpcomingMaintenanceReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(10);

        List<Vehicle> upcomingVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getNextDate() != null && v.getNextDate().toLocalDate().equals(reminderDate))
                .toList();

        for (Vehicle vehicle : upcomingVehicles) {
            AppUser owner = vehicle.getOwner();
            if (owner != null) {
                try {
                    emailService.sendMaintenanceReminderEmail(owner, vehicle, vehicle.getNextDate().toLocalDate());

                    // --- THONG BAO NHAC LICH ---
                    notificationService.createNotification(owner,
                            "Maintenance Reminder",
                            "Your vehicle " + vehicle.getLicensePlate() + " is due for maintenance on " + reminderDate,
                            "REMINDER");

                } catch (Exception e) {
                    System.err.println("Failed to send reminder to " + owner.getEmail() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Maintenance reminder emails sent for vehicles due on: " + reminderDate);
    }


    @Override
    @Transactional
    public void updateUsedParts(Long ticketId, List<UsedPartDto> usedParts) {
        // 1. Tìm phiếu dịch vụ
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        // --- QUAN TRỌNG: Xác định Center của phiếu này ---
        if (ticket.getCenter() == null) {
            throw new IllegalStateException("This ticket does not belong to any center. Cannot deduct stock.");
        }
        Long currentCenterId = ticket.getCenter().getId();
        String centerName = ticket.getCenter().getCenterName();

        // 2. Hoàn trả lại kho các phụ tùng cũ (nếu sửa lại danh sách)
        List<MaintenanceItemPart> currentParts = maintenanceItemPartRepository.findByMaintenanceHistoryId(ticketId);
        for (MaintenanceItemPart oldPart : currentParts) {
            // Tìm kho của đúng Center này để cộng lại
            Inventory inventory = inventoryRepository.findByCenterIdAndSparePartId(currentCenterId, oldPart.getSparePart().getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory record missing for part: " + oldPart.getSparePart().getPartName() + " at " + centerName));

            inventory.setStockQuantity(inventory.getStockQuantity() + oldPart.getQuantity());
            inventoryRepository.save(inventory);
        }

        // 3. Xóa danh sách cũ
        maintenanceItemPartRepository.deleteByMaintenanceHistoryId(ticketId);

        // 4. Thêm mới và Trừ kho
        if (usedParts != null && !usedParts.isEmpty()) {
            List<MaintenanceItemPart> newItemParts = new ArrayList<>();

            for (UsedPartDto partDto : usedParts) {
                SparePart sparePart = sparePartRepository.findById(partDto.partId())
                        .orElseThrow(() -> new EntityNotFoundException("Spare part not found"));

                // --- QUAN TRỌNG: Tìm trong kho của CENTER ĐÓ ---
                Inventory inventory = inventoryRepository.findByCenterIdAndSparePartId(currentCenterId, sparePart.getId())
                        .orElseThrow(() -> new IllegalStateException("Part '" + sparePart.getPartName() + "' is NOT available at " + centerName));

                // Kiểm tra số lượng
                if (inventory.getStockQuantity() < partDto.quantity()) {
                    throw new IllegalStateException("Not enough stock at " + centerName +
                            ". Part: " + sparePart.getPartName() +
                            ". Available: " + inventory.getStockQuantity() +
                            ", Requested: " + partDto.quantity());
                }

                // Trừ kho
                inventory.setStockQuantity(inventory.getStockQuantity() - partDto.quantity());
                inventoryRepository.save(inventory);

                // --- XỬ LÝ TODO: CẢNH BÁO HẾT HÀNG ---
                if (inventory.getStockQuantity() <= inventory.getMinStockLevel()) {
                    // Gửi thông báo cho Staff quản lý đơn này
                    AppUser staff = ticket.getStaff();
                    if (staff != null) {
                        notificationService.createNotification(staff,
                                "Low Stock Alert",
                                "Part '" + sparePart.getPartName() + "' is running low at " + centerName + ". Current: " + inventory.getStockQuantity(),
                                "INVENTORY");
                    }
                }
                // ---------------------------------------

                // Tạo record sử dụng
                MaintenanceItemPart itemPart = new MaintenanceItemPart();
                itemPart.setMaintenanceHistory(ticket);
                itemPart.setSparePart(sparePart);
                itemPart.setQuantity(partDto.quantity());
                // Lưu giá tại thời điểm dùng (nếu entity có trường này, nếu không thì bỏ dòng dưới)
                // itemPart.setPriceAtTimeOfUse(sparePart.getUnitPrice());
                newItemParts.add(itemPart);
            }
            maintenanceItemPartRepository.saveAll(newItemParts);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsedPartDto> getUsedParts(Long ticketId) {
        return maintenanceItemPartRepository.findByMaintenanceHistoryId(ticketId)
                .stream()
                .map(item -> new UsedPartDto(item.getSparePart().getId(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addOrUpdateAdditionalCost(Long ticketId, BigDecimal amount, String reason) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance ticket not found with id: " + ticketId));

        ticket.setHasAdditionalCost(true);
        ticket.setAdditionalCostAmount(amount);
        ticket.setAdditionalCostReason(reason);
        ticket.setStatus(MaintenanceStatus.PENDING_APPROVAL);

        maintenanceHistoryRepository.save(ticket);

        emailService.sendAdditionalCostApprovalRequestEmail(ticket.getOwner(), ticket);

        // --- THONG BAO CUSTOMER ---
        notificationService.createNotification(ticket.getOwner(),
                "Cost Approval Required",
                "Technician has reported additional costs. Please review and approve.",
                "APPROVAL");
    }

    @Override
    @Transactional
    public void approveAdditionalCost(Long ticketId, OidcUser oidcUser) {
        AppUser currentUser = userService.getCurrentUser(oidcUser);

        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance ticket not found with id: " + ticketId));

        if (!ticket.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to approve costs for this ticket.");
        }

        if (ticket.getStatus() != MaintenanceStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("This ticket is not awaiting customer approval.");
        }

        ticket.setStatus(MaintenanceStatus.CUSTOMER_APPROVED);
        maintenanceHistoryRepository.save(ticket);

        AppUser staffAssigned = ticket.getStaff();
        if (staffAssigned != null) {
            emailService.sendCostApprovedNotificationToStaff(staffAssigned, ticket);

            // --- THONG BAO STAFF ---
            notificationService.createNotification(staffAssigned,
                    "Cost Approved",
                    "Customer approved the additional cost for ticket #" + ticket.getId(),
                    "APPROVAL");
        }
    }

    @Override
    public void handoverCarToCustomer(Long id) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_COMPLETED) {
            throw new RuntimeException("Invalid status for handover. Current: " + ticket.getStatus());
        }

        ticket.setStatus(MaintenanceStatus.DONE);
        ticket.setHandOverAt(LocalDateTime.now());
        maintenanceHistoryRepository.save(ticket);

        // --- THONG BAO KHACH HANG ---
        if (ticket.getOwner() != null) {
            notificationService.createNotification(ticket.getOwner(),
                    "Service Completed",
                    "Your vehicle is ready for pickup. Thank you for using Ecar Service.",
                    "DONE");
        }
    }


    @Override
    @Transactional
    public void requestAdditionalCost(AdditionalCostRequest request) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(request.ticketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_RECEIVED) {
            throw new RuntimeException("Invalid status. Must be IN PROGRESS.");
        }

        ticket.setHasAdditionalCost(true);
        ticket.setAdditionalCostAmount(request.amount());
        ticket.setAdditionalCostReason(request.reason());
        ticket.setStatus(MaintenanceStatus.PENDING_APPROVAL);

        MaintenanceHistory savedTicket = maintenanceHistoryRepository.save(ticket);

        AppUser staff = savedTicket.getStaff();
        if (staff != null) {
            try {
                emailService.sendAdditionalCostRequestEmail(staff, savedTicket);

                // --- THONG BAO STAFF ---
                notificationService.createNotification(staff,
                        "Cost Approval Request",
                        "Technician requested additional cost for ticket #" + ticket.getId(),
                        "APPROVAL");
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void processCustomerDecision(Long id, String decision) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != MaintenanceStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Ticket is not pending approval.");
        }

        if ("APPROVE".equalsIgnoreCase(decision)) {
            ticket.setStatus(MaintenanceStatus.TECHNICIAN_RECEIVED);
            ticket.setRemark(ticket.getRemark() + " | Customer Approved Additional Cost.");

            // --- THONG BAO TECHNICIAN ---
            if(ticket.getTechnician() != null) {
                notificationService.createNotification(ticket.getTechnician(),
                        "Cost Approved",
                        "Customer approved the cost. You can resume work on ticket #" + ticket.getId(),
                        "TASK");
            }

        } else if ("REJECT".equalsIgnoreCase(decision)) {
            ticket.setStatus(MaintenanceStatus.CANCELLED);
            ticket.setRemark(ticket.getRemark() + " | Customer Rejected Cost -> Cancelled.");

            if (ticket.getOwner() != null) {
                emailService.sendOrderCancelledEmail(ticket.getOwner(), ticket, "Customer declined additional service costs");
            }
        } else {
            throw new RuntimeException("Invalid decision. Use APPROVE or REJECT.");
        }

        maintenanceHistoryRepository.save(ticket);
    }

    @Override
    @Transactional
    public void declineTask(Long id) {
        MaintenanceHistory ticket = maintenanceHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        if (ticket.getStatus() != MaintenanceStatus.TECHNICIAN_RECEIVED) {
            throw new IllegalStateException("Can only decline tasks that are currently assigned.");
        }

        AppUser technician = ticket.getTechnician();
        AppUser staff = ticket.getStaff();

        ticket.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);
        ticket.setTechnician(null);
        ticket.setTechnicianReceivedAt(null);

        maintenanceHistoryRepository.save(ticket);

        if (staff != null && technician != null) {
            emailService.sendTaskDeclinedEmail(staff, technician, ticket);

            // --- THONG BAO STAFF ---
            notificationService.createNotification(staff,
                    "Task Declined",
                    "Technician " + technician.getFullName() + " declined task #" + id + ". Please reassign.",
                    "URGENT");
        }
    }
}