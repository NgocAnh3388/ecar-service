package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.payload.requests.MaintenanceScheduleRequest;
import com.ecar.ecarservice.payload.requests.ServiceCreateRequest;
import com.ecar.ecarservice.payload.responses.MaintenanceTicketResponse;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import com.ecar.ecarservice.repositories.MaintenanceItemRepository;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceImplTest {

    @Mock
    private MaintenanceHistoryRepository maintenanceHistoryRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private MaintenanceItemRepository maintenanceItemRepository;
    @Mock
    private UserService userService;
    // Các repository khác không có logic phức tạp nên có thể không cần mock hết
    // @Mock private VehicleRepository vehicleRepository;
    // @Mock private CenterRepository centerRepository;

    @InjectMocks
    private MaintenanceServiceImpl maintenanceService;

    private AppUser staffUser;
    private AppUser technicianUser;
    private OidcUser oidcUser;
    private MaintenanceHistory testTicket;

    @BeforeEach
    void setUp() {
        oidcUser = mock(OidcUser.class);

        staffUser = new AppUser();
        staffUser.setId(1L);
        staffUser.setRoles(Set.of(AppRole.STAFF));

        technicianUser = new AppUser();
        technicianUser.setId(2L);
        technicianUser.setRoles(Set.of(AppRole.TECHNICIAN));

        testTicket = new MaintenanceHistory();
        testTicket.setId(100L);
        testTicket.setStatus(MaintenanceStatus.CUSTOMER_SUBMITTED);
    }

    @Test
    @DisplayName("createService_Success: Staff should successfully assign a ticket to a technician")
    void testCreateService_Success() {
        // Arrange
        ServiceCreateRequest request = new ServiceCreateRequest(100L, 50000L, 1L, 2L, List.of(10L, 11L));

        when(userService.getCurrentUser(oidcUser)).thenReturn(staffUser);
        when(maintenanceHistoryRepository.findById(request.ticketId())).thenReturn(Optional.of(testTicket));
        when(appUserRepository.findById(request.technicianId())).thenReturn(Optional.of(technicianUser));
        when(maintenanceHistoryRepository.save(any(MaintenanceHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        maintenanceService.createService(request, oidcUser);

        // Assert
        ArgumentCaptor<MaintenanceHistory> ticketCaptor = ArgumentCaptor.forClass(MaintenanceHistory.class);
        verify(maintenanceHistoryRepository, times(1)).save(ticketCaptor.capture());
        MaintenanceHistory savedTicket = ticketCaptor.getValue();

        assertEquals(MaintenanceStatus.TECHNICIAN_RECEIVED, savedTicket.getStatus());
        assertEquals(staffUser, savedTicket.getStaff());
        assertEquals(technicianUser, savedTicket.getTechnician());
        assertNotNull(savedTicket.getTechnicianReceivedAt());

        verify(maintenanceItemRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("createService_TicketNotFound: Should throw EntityNotFoundException")
    void testCreateService_TicketNotFound() {
        // Arrange
        ServiceCreateRequest request = new ServiceCreateRequest(999L, 50000L, 1L, 2L, List.of());
        when(userService.getCurrentUser(oidcUser)).thenReturn(staffUser);
        when(maintenanceHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            maintenanceService.createService(request, oidcUser);
        });

        verify(maintenanceHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("createService_TechnicianNotFound: Should throw EntityNotFoundException")
    void testCreateService_TechnicianNotFound() {
        // Arrange
        ServiceCreateRequest request = new ServiceCreateRequest(100L, 50000L, 1L, 999L, List.of()); // Technician ID không tồn tại
        when(userService.getCurrentUser(oidcUser)).thenReturn(staffUser);
        when(maintenanceHistoryRepository.findById(request.ticketId())).thenReturn(Optional.of(testTicket));
        when(appUserRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            maintenanceService.createService(request, oidcUser);
        });

        verify(maintenanceHistoryRepository, never()).save(any());
    }


    @Test
    @DisplayName("getTicketsForTechnician_Success: Should return only tickets for the current technician")
    void testGetTicketsForTechnician_Success() {
        // Arrange
        when(userService.getCurrentUser(oidcUser)).thenReturn(technicianUser);

        // --- KHỞI TẠO ĐẦY ĐỦ DỮ LIỆU GIẢ LẬP ---
        MaintenanceHistory ticketForMe = new MaintenanceHistory();
        ticketForMe.setId(101L);

        AppUser owner = new AppUser();
        owner.setFullName("Test Owner");
        ticketForMe.setOwner(owner);

        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setCarName("VF8");

        Vehicle vehicle = new Vehicle();
        vehicle.setCarModel(carModel);
        vehicle.setLicensePlate("123-TEST");
        ticketForMe.setVehicle(vehicle);

        Center center = new Center();
        center.setCenterName("Test Center");
        ticketForMe.setCenter(center);

        // Giả lập repository trả về danh sách chứa ticket đã có đủ dữ liệu
        when(maintenanceHistoryRepository.findByTechnicianIdOrderByStatus(technicianUser.getId())).thenReturn(List.of(ticketForMe));

        // Act
        List<MaintenanceTicketResponse> result = maintenanceService.getTicketsForTechnician(oidcUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Owner", result.get(0).customerName());
        assertEquals("123-TEST", result.get(0).licensePlate());
        verify(maintenanceHistoryRepository, times(1)).findByTechnicianIdOrderByStatus(technicianUser.getId());
    }}