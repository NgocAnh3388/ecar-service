package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AppUser testUser1;
    private AppUser testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new AppUser();
        testUser1.setId(1L);
        testUser1.setEmail("user1@example.com");
        testUser1.setActive(true);
        testUser1.setRoles(Set.of(AppRole.CUSTOMER));

        testUser2 = new AppUser();
        testUser2.setId(2L);
        testUser2.setEmail("user2@example.com");
        testUser2.setActive(true);
        testUser2.setRoles(Set.of(AppRole.STAFF));
    }

    // =============================================
    //         HAPPYCASE
    // =============================================

    @Test
    @DisplayName("getAllUsers: Should return a list of active users")
    void testGetAllUsers() {
        when(appUserRepository.findAllByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(testUser1, testUser2));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
        verify(appUserRepository, times(1)).findAllByActiveTrueOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("convertToDto: Should map basic fields and empty vehicles safely")
    void testConvertToDto_BasicMapping_NoVehicles() throws Exception {
        UserDto dto = invokeConvertToDto(testUser1);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("user1@example.com");
        assertThat(dto.getVehicles()).isNull();
    }

    @Test
    @DisplayName("convertToDto: Should map vehicles when present")
    void testConvertToDto_WithVehicles() throws Exception {
        Vehicle v = new Vehicle();
        v.setLicensePlate("30A-12345");
        v.setVinNumber("12345678901234567");
        v.setNextKm(10000L);
        v.setNextDate(LocalDateTime.now().plusDays(30));
        v.setOldKm(5000L);
        v.setOldDate(LocalDateTime.now().minusDays(30));
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(v);
        testUser1.setVehicles(vehicles);

        UserDto dto = invokeConvertToDto(testUser1);

        assertThat(dto.getVehicles()).hasSize(1);
        VehicleDto vd = dto.getVehicles().get(0);
        assertThat(vd.getLicensePlate()).isEqualTo("30A-12345");
        assertThat(vd.getVinNumber()).isEqualTo("12345678901234567");
        assertThat(vd.getNextKm()).isEqualTo(10000L);
        assertThat(vd.getOldKm()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("getUserByEmail: Should load by email and map to dto")
    void testGetUserByEmail() {
        when(appUserRepository.findByEmailWithVehicles("user1@example.com")).thenReturn(Optional.of(testUser1));
        UserDto dto = userService.getUserByEmail("user1@example.com");
        assertThat(dto.getEmail()).isEqualTo("user1@example.com");
        verify(appUserRepository).findByEmailWithVehicles("user1@example.com");
    }

    @Test
    @DisplayName("getUserById: Should throw when not found")
    void testGetUserById_NotFound() {
        when(appUserRepository.findByIdWithVehicles(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("searchUsers: Should return empty page when ids page empty")
    void testSearchUsers_EmptyIds() {
        UserSearchRequest request = new UserSearchRequest();
        request.setPage(0);
        request.setSize(10);
        request.setSearchValue("abc");
        PageRequest pr = PageRequest.of(0,10);
        when(appUserRepository.searchUserIdsByValue("abc", pr)).thenReturn(Page.empty());
        Page<AppUser> page = userService.searchUsers(request);
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getContent()).isEmpty();
    }

    // =============================================
    //         UNHAPPYCASE (edge behaviors around convertToDto)
    // =============================================

    @Test
    @DisplayName("convertToDto: Should ignore empty vehicles list")
    void testConvertToDto_EmptyVehiclesList() throws Exception {
        testUser1.setVehicles(new ArrayList<>());
        UserDto dto = invokeConvertToDto(testUser1);
        assertThat(dto.getVehicles()).isNull();
    }

    // ===================== helpers =====================
    private UserDto invokeConvertToDto(AppUser user) throws Exception {
        var method = UserServiceImpl.class.getDeclaredMethod("convertToDto", AppUser.class);
        method.setAccessible(true);
        return (UserDto) method.invoke(userService, user);
    }
}