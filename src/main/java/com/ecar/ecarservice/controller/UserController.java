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
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // <--- KIỂM TRA DÒNG NÀY

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Get all users
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. Get user by ID
    @GetMapping("/{id:[0-9]+}") // Chỉ match số để tránh xung đột với /search
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userServiceImpl.getUserById(#id).email")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // 3. Create user
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
        return ResponseEntity.ok().build();
    }

    // 4. Update user
    @PutMapping("/{id:[0-9]+}")
    // Cho phép nều là ADMIN  -HOẶC-  Email của người gửi request trùng với email trong body update
    @PreAuthorize("hasRole('ADMIN') or #userUpdateDTO.email == authentication.principal.email")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }

    // 5. Delete user
    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Search users (POST /api/users/search)
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Page<AppUser>> searchUsers(@RequestBody UserSearchRequest request) {
        Page<AppUser> searchResult = userService.searchUsers(request);
        return ResponseEntity.ok(searchResult);
    }

    // 7. Get users by role
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<AppUser>> getUserListByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUserListByRole(roleName));
    }

    // 8. Get current logged-in user info
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null || oidcUser.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDto userDto = userService.getUserByEmail(oidcUser.getEmail());
        return ResponseEntity.ok(userDto);
    }

    // 9. Toggle active status
    @PutMapping("/{id:[0-9]+}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        userService.toggleActiveUser(id);
        return ResponseEntity.ok().build();
    }

    // 10. Get technicians by center
    @GetMapping("/technicians/by-center/{centerId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<UserDto>> getTechniciansByCenter(@PathVariable Long centerId) {
        List<AppUser> technicians = userService.getTechniciansByCenter(centerId);
        List<UserDto> technicianDtos = technicians.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(technicianDtos);
    }

    // Chuyển AppUser → UserDto
    private UserDto convertToDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());

        if (user.getVehicles() != null && !user.getVehicles().isEmpty()) {
            List<VehicleDto> vehicleDtos = user.getVehicles().stream().map(v -> {
                VehicleDto vehicleDto = new VehicleDto();
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
    @GetMapping("/get-by-role/technician")
    public ResponseEntity<List<UserDto>> getTechnicians() {
        List<AppUser> techs = userService.getUserListByRole("TECHNICIAN");
        List<UserDto> dtos = techs.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}
