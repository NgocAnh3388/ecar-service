package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Get List of Users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. Get User with ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<AppUser> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(userService.getCurrentUser(oidcUser));
    }

    // 3. Update User (ví dụ: chỉ cập nhật role)
    @PutMapping("/{id}") // SỬA: Dùng @PutMapping và lấy ID từ đường dẫn
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDTO userUpdateDTO) {
        // Gọi service với cả id và DTO chứa thông tin mới
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }

    // 4. Delete User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // THÊM: Chỉ Admin mới được xóa
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        this.userService.createUser(userCreateDTO);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<AppUser>> searchUsers(@RequestBody UserSearchRequest request) {
        Page<AppUser> searchResult = userService.searchUsers(request);
        return new  ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @RequestMapping(value = "get-by-role/{roleName}", method = RequestMethod.GET)
    public ResponseEntity<List<AppUser>> getUserListByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(this.userService.getUserListByRole(roleName));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null || oidcUser.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Lấy user từ DB để có vehicles
        UserDto userDto = userService.getUserByEmail(oidcUser.getEmail());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        userService.toggleActiveUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/technicians/by-center/{centerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<UserDto>> getTechniciansByCenter(@PathVariable Long centerId) {
        // Gọi đến service để lấy danh sách Entity
        List<AppUser> technicians = userService.getTechniciansByCenter(centerId);

        // Chuyển đổi danh sách Entity sang danh sách DTO
        List<UserDto> technicianDtos = technicians.stream()
                .map(this::convertToDto) // Tái sử dụng hàm convertToDto bạn đã có
                .collect(Collectors.toList());

        return ResponseEntity.ok(technicianDtos);
    }

    private UserDto convertToDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());

        // Chuyển đổi thông tin xe của người dùng (nếu có)
        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            List<VehicleDto> vehicleDtos = user.getVehicles().stream().map(v -> {
                VehicleDto vehicleDto = new VehicleDto();
                // Giả định VehicleDto có các setter tương ứng
                vehicleDto.setLicensePlate(v.getLicensePlate());
                vehicleDto.setCarModel(v.getCarModel());
                vehicleDto.setVinNumber(v.getVinNumber());
                vehicleDto.setNextKm(v.getNextKm());
                vehicleDto.setNextDate(v.getNextDate());
                vehicleDto.setOldKm(v.getOldKm());
                vehicleDto.setOldDate(v.getOldDate());
                return vehicleDto;
            }).collect(Collectors.toList());
            dto.setVehicles(vehicleDtos);
        }

        return dto;
    }

}
