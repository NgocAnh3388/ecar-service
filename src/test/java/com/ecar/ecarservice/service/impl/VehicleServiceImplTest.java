//package com.ecar.ecarservice.service.impl;
//
//import com.ecar.ecarservice.dto.VehicleDto;
//import com.ecar.ecarservice.entities.AppUser;
//import com.ecar.ecarservice.entities.CarModel;
//import com.ecar.ecarservice.entities.Vehicle;
//import com.ecar.ecarservice.payload.requests.VehicleRequest;
//import com.ecar.ecarservice.payload.responses.VehicleResponse;
//import com.ecar.ecarservice.repositories.CarModelRepository;
//import com.ecar.ecarservice.repositories.VehicleRepository;
//import com.ecar.ecarservice.service.UserService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class VehicleServiceImplTest {
//
//    @Mock
//    private VehicleRepository vehicleRepository;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private CarModelRepository carModelRepository;
//
//    @InjectMocks
//    private VehicleServiceImpl vehicleService;
//
//    private AppUser testUser;
//    private OidcUser oidcUser;
//    private CarModel testCarModel;
//    private Vehicle testVehicle;
//
//    @BeforeEach
//    void setUp() {
//        oidcUser = mock(OidcUser.class);
//
//        testUser = new AppUser();
//        testUser.setId(1L);
//
//        testCarModel = new CarModel();
//        testCarModel.setId(10L);
//        testCarModel.setCarName("Test Model");
//
//        testVehicle = new Vehicle();
//        testVehicle.setId(100L);
//        testVehicle.setOwnerId(testUser.getId());
//        testVehicle.setLicensePlate("12A-345.67");
//        testVehicle.setCarModel(testCarModel);
//        testVehicle.setActive(true);
//    }
//
//    // =============================================
//    //         HAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("getMyVehicles: Should return list of vehicles for the current user")
//    void testGetMyVehicles() {
//        // Arrange
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(vehicleRepository.findByOwnerIdAndActiveTrue(testUser.getId())).thenReturn(List.of(testVehicle));
//
//        // Act
//        List<VehicleResponse> result = vehicleService.getMyVehicles(oidcUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("12A-345.67", result.get(0).licensePlate());
//    }
//
//    @Test
//    @DisplayName("updateVehicle_Success: Should update vehicle details")
//    void testUpdateVehicle_Success() {
//        // Arrange
//        Long vehicleId = 100L;
//        VehicleDto updateDto = new VehicleDto();
//        updateDto.setLicensePlate("99Z-999.99");
//        updateDto.setVinNumber("NEW_VIN");
//        updateDto.setCarModel(testCarModel);
//
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(vehicleRepository.findByIdAndOwnerIdAndActiveTrue(vehicleId, testUser.getId())).thenReturn(Optional.of(testVehicle));
//        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        VehicleDto result = vehicleService.updateVehicle(vehicleId, updateDto, oidcUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("99Z-999.99", result.getLicensePlate());
//        assertEquals("NEW_VIN", result.getVinNumber());
//
//        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
//    }
//
//
//
//    @Test
//    @DisplayName("addVehicle_Success: Should save a new vehicle for the current user")
//    void testAddVehicle_Success() {
//        // Arrange
//        VehicleRequest request = new VehicleRequest(10L, "51K-55555", "VIN55555");
//
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(carModelRepository.getReferenceById(10L)).thenReturn(testCarModel);
//
//        // Act
//        vehicleService.addVehicle(request, oidcUser);
//
//        // Assert
//        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
//        verify(vehicleRepository, times(1)).save(vehicleCaptor.capture());
//        Vehicle savedVehicle = vehicleCaptor.getValue();
//
//        assertEquals(testUser.getId(), savedVehicle.getOwnerId());
//        assertEquals("51K-55555", savedVehicle.getLicensePlate());
//    }
//
//    @Test
//    @DisplayName("deleteVehicle_Success: Should set vehicle to inactive")
//    void testDeleteVehicle_Success() {
//        // Arrange
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(vehicleRepository.findByIdAndOwnerIdAndActiveTrue(100L, testUser.getId())).thenReturn(Optional.of(testVehicle));
//
//        // Act
//        vehicleService.deleteVehicle(100L, oidcUser);
//
//        // Assert
//        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
//        verify(vehicleRepository, times(1)).save(vehicleCaptor.capture());
//        Vehicle savedVehicle = vehicleCaptor.getValue();
//
//        assertFalse(savedVehicle.isActive());
//    }
//
//    @Test
//    @DisplayName("deleteVehicle_NotFound: Should throw EntityNotFoundException")
//    void testDeleteVehicle_NotFound_ThrowsException() {
//        // Arrange
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(vehicleRepository.findByIdAndOwnerIdAndActiveTrue(99L, testUser.getId())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            vehicleService.deleteVehicle(99L, oidcUser);
//        });
//    }
//
//    // =============================================
//    //         UNHAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("updateVehicle_NotFound: Should throw EntityNotFoundException")
//    void testUpdateVehicle_NotFound_ThrowsException() {
//        // Arrange
//        Long nonExistentVehicleId = 99L;
//        VehicleDto updateDto = new VehicleDto();
//        updateDto.setLicensePlate("anything");
//
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        // Giả lập việc không tìm thấy vehicle
//        when(vehicleRepository.findByIdAndOwnerIdAndActiveTrue(nonExistentVehicleId, testUser.getId()))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            vehicleService.updateVehicle(nonExistentVehicleId, updateDto, oidcUser);
//        });
//
//        verify(vehicleRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("getMyVehicles_ReturnsEmptyList_WhenUserHasNoVehicles")
//    void testGetMyVehicles_ReturnsEmptyList() {
//        // Arrange
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        when(vehicleRepository.findByOwnerIdAndActiveTrue(testUser.getId())).thenReturn(List.of());
//
//        // Act
//        List<VehicleResponse> result = vehicleService.getMyVehicles(oidcUser);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("addVehicle_Fails_WhenCarModelNotFound")
//    void testAddVehicle_Fails_WhenCarModelNotFound() {
//        // Arrange
//        Long nonExistentCarModelId = 99L;
//        VehicleRequest request = new VehicleRequest(nonExistentCarModelId, "51K-55555", "VIN55555");
//
//        when(userService.getCurrentUser(oidcUser)).thenReturn(testUser);
//        // Giả lập việc getReferenceById ném ra EntityNotFoundException
//        when(carModelRepository.getReferenceById(nonExistentCarModelId)).thenThrow(new EntityNotFoundException());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            vehicleService.addVehicle(request, oidcUser);
//        });
//
//        verify(vehicleRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("updateVehicle_Fails_WhenUserIsNotOwner")
//    void testUpdateVehicle_Fails_WhenUserIsNotOwner() {
//        // Arrange
//        Long vehicleId = 100L;
//        VehicleDto updateDto = new VehicleDto();
//
//        AppUser anotherUser = new AppUser();
//        anotherUser.setId(2L); // User khác
//
//        when(userService.getCurrentUser(oidcUser)).thenReturn(anotherUser);
//        // Giả lập rằng không tìm thấy xe với ID 100 và ownerId 2L
//        when(vehicleRepository.findByIdAndOwnerIdAndActiveTrue(vehicleId, anotherUser.getId())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            // User 2 cố gắng cập nhật xe của user 1
//            vehicleService.updateVehicle(vehicleId, updateDto, oidcUser);
//        });
//    }
//
//}