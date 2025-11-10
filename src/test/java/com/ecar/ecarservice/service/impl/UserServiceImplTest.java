//package com.ecar.ecarservice.service.impl;
//
//import com.ecar.ecarservice.dto.UserCreateDTO;
//import com.ecar.ecarservice.dto.UserDto;
//import com.ecar.ecarservice.entities.AppUser;
//import com.ecar.ecarservice.enums.AppRole;
//import com.ecar.ecarservice.payload.requests.UserSearchRequest;
//import com.ecar.ecarservice.repositories.AppUserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceImplTest {
//
//    @Mock
//    private AppUserRepository appUserRepository;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private AppUser testUser1;
//    private AppUser testUser2;
//
//    @BeforeEach
//    void setUp() {
//        testUser1 = new AppUser();
//        testUser1.setId(1L);
//        testUser1.setEmail("user1@example.com");
//        testUser1.setActive(true);
//        testUser1.setRoles(Set.of(AppRole.CUSTOMER));
//
//        testUser2 = new AppUser();
//        testUser2.setId(2L);
//        testUser2.setEmail("user2@example.com");
//        testUser2.setActive(true);
//        testUser2.setRoles(Set.of(AppRole.STAFF));
//    }
//
//    // =============================================
//    //         HAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("getAllUsers: Should return a list of active users")
//    void testGetAllUsers() {
//        // Arrange
//        when(appUserRepository.findAllByActiveTrue()).thenReturn(List.of(testUser1, testUser2));
//
//        // Act
//        List<UserDto> result = userService.getAllUsers();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("user1@example.com", result.get(0).getEmail());
//        verify(appUserRepository, times(1)).findAllByActiveTrue();
//    }
//
//    @Test
//    @DisplayName("getUserById_Success: Should return user DTO when user is found and active")
//    void testGetUserById_Success() {
//        // Arrange
//        when(appUserRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(testUser1));
//
//        // Act
//        UserDto result = userService.getUserById(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        assertEquals("user1@example.com", result.getEmail());
//    }
//
//    @Test
//    @DisplayName("getUserById_NotFound: Should throw EntityNotFoundException")
//    void testGetUserById_NotFound() {
//        // Arrange
//        when(appUserRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            userService.getUserById(99L);
//        });
//    }
//
//    @Test
//    @DisplayName("createUser: Should save a new user with correct details")
//    void testCreateUser() {
//        // Arrange
//        UserCreateDTO createRequest = new UserCreateDTO();
//        createRequest.setEmail("newuser@example.com");
//        createRequest.setFullName("New User");
//        createRequest.setPhoneNo("123456789");
//        createRequest.setRole("TECHNICIAN");
//
//        // Act
//        userService.createUser(createRequest);
//
//        // Assert
//        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
//        verify(appUserRepository, times(1)).save(userCaptor.capture());
//        AppUser savedUser = userCaptor.getValue();
//
//        assertEquals("newuser@example.com", savedUser.getEmail());
//        assertEquals("New User", savedUser.getFullName());
//        assertTrue(savedUser.getRoles().contains(AppRole.TECHNICIAN));
//    }
//
//    @Test
//    @DisplayName("getCurrentUser: Should return user from OidcUser subject")
//    void testGetCurrentUser() {
//        // Arrange
//        OidcUser oidcUser = mock(OidcUser.class);
//        when(oidcUser.getSubject()).thenReturn("google-sub-123");
//        when(appUserRepository.findBySub("google-sub-123")).thenReturn(Optional.of(testUser1));
//
//        // Act
//        AppUser result = userService.getCurrentUser(oidcUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//    }
//
//
//
//    @Test
//    @DisplayName("updateUser_Success: Should update user details and return DTO")
//    void testUpdateUser_Success() {
//        // Arrange
//        UserCreateDTO updateRequest = new UserCreateDTO();
//        updateRequest.setEmail("user1@example.com");
//        updateRequest.setFullName("New Full Name");
//        updateRequest.setPhoneNo("0987654321");
//        updateRequest.setRole("STAFF");
//
//        when(appUserRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(testUser1));
//        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        UserDto result = userService.updateUser(updateRequest);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        assertTrue(result.getRoles().contains(AppRole.STAFF));
//    }
//
//    @Test
//    @DisplayName("updateUser_UserNotFound: Should throw EntityNotFoundException")
//    void testUpdateUser_UserNotFound_ThrowsException() {
//        // Arrange
//        UserCreateDTO updateRequest = new UserCreateDTO();
//        updateRequest.setEmail("notfound@example.com");
//        when(appUserRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            userService.updateUser(updateRequest);
//        });
//    }
//
//    @Test
//    @DisplayName("deleteUser_Success: Should set user to inactive")
//    void testDeleteUser_Success() {
//        // Arrange
//        when(appUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
//
//        // Act
//        userService.deleteUser(1L);
//
//        // Assert
//        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
//        verify(appUserRepository).save(userCaptor.capture());
//        AppUser savedUser = userCaptor.getValue();
//
//        assertFalse(savedUser.isActive());
//    }
//
//    @Test
//    @DisplayName("deleteUser_UserNotFound: Should throw EntityNotFoundException")
//    void testDeleteUser_UserNotFound_ThrowsException() {
//        // Arrange
//        when(appUserRepository.findById(99L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            userService.deleteUser(99L);
//        });
//    }
//
//
//    @Test
//    @DisplayName("searchUsers: Should call repository search method")
//    void testSearchUsers() {
//        // Arrange
//        UserSearchRequest searchRequest = new UserSearchRequest();
//        searchRequest.setSearchValue("test");
//        searchRequest.setPage(0);
//        searchRequest.setSize(10);
//        PageRequest pageRequest = PageRequest.of(0, 10);
//
//        // Giả lập repository trả về một trang trống
//        when(appUserRepository.searchAppUserByValue("test", pageRequest)).thenReturn(Page.empty());
//
//        // Act
//        Page<AppUser> result = userService.searchUsers(searchRequest);
//
//        // Assert
//        assertNotNull(result);
//        verify(appUserRepository, times(1)).searchAppUserByValue("test", pageRequest);
//    }
//
//    @Test
//    @DisplayName("getUserListByRole: Should return list of users with a specific role")
//    void testGetUserListByRole() {
//        // Arrange
//        String roleName = "TECHNICIAN";
//        AppUser techUser = new AppUser();
//        techUser.setRoles(Set.of(AppRole.TECHNICIAN));
//
//        when(appUserRepository.findByRoles(AppRole.TECHNICIAN)).thenReturn(List.of(techUser));
//
//        // Act
//        List<AppUser> result = userService.getUserListByRole(roleName);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(appUserRepository, times(1)).findByRoles(AppRole.TECHNICIAN);
//    }
//
//    @Test
//    @DisplayName("getCurrentUser_NotFound: Should throw RuntimeException")
//    void testGetCurrentUser_NotFound() {
//        // Arrange
//        OidcUser oidcUser = mock(OidcUser.class);
//        when(oidcUser.getSubject()).thenReturn("unknown-sub");
//        when(appUserRepository.findBySub("unknown-sub")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> {
//            userService.getCurrentUser(oidcUser);
//        });
//    }
//
//    // =============================================
//    //         UNHAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("getAllUsers_ReturnsEmptyList_WhenNoActiveUsersExist")
//    void testGetAllUsers_ReturnsEmptyList() {
//        // Arrange
//        when(appUserRepository.findAllByActiveTrue()).thenReturn(List.of());
//
//        // Act
//        List<UserDto> result = userService.getAllUsers();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("updateUser_Fails_WhenRoleIsInvalid")
//    void testUpdateUser_Fails_WhenRoleIsInvalid() {
//        // Arrange
//        UserCreateDTO updateRequest = new UserCreateDTO();
//        updateRequest.setEmail("user1@example.com");
//        updateRequest.setRole("INVALID_ROLE"); // Role không hợp lệ
//
//        when(appUserRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(testUser1));
//
//        // Act & Assert
//        // Enum.valueOf sẽ ném ra IllegalArgumentException nếu giá trị không tồn tại
//        assertThrows(IllegalArgumentException.class, () -> {
//            userService.updateUser(updateRequest);
//        });
//
//        verify(appUserRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("getUserListByRole_ReturnsEmptyList_WhenNoUsersWithRole")
//    void testGetUserListByRole_ReturnsEmptyList() {
//        // Arrange
//        when(appUserRepository.findByRoles(AppRole.TECHNICIAN)).thenReturn(List.of());
//
//        // Act
//        List<AppUser> result = userService.getUserListByRole("TECHNICIAN");
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//}