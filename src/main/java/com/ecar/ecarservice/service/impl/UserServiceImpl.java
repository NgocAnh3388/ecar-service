package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import com.ecar.ecarservice.dto.VehicleDto;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;

    public UserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
//        return appUserRepository.findAllByActiveTrue().stream()
        return appUserRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        // Dùng join fetch để load vehicles + carModel
        AppUser user = appUserRepository.findByIdWithVehicles(id)
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserCreateDTO userUpdateDTO) {
        // Bước 1: Tìm người dùng theo ID từ URL
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Bước 2: Xử lý cập nhật email, đảm bảo tính duy nhất
        if (!user.getEmail().equalsIgnoreCase(userUpdateDTO.getEmail())) {
            // Nếu email thay đổi, kiểm tra xem email mới đã tồn tại chưa
            appUserRepository.findByEmail(userUpdateDTO.getEmail()).ifPresent(existingUser -> {
                throw new IllegalStateException("Email " + userUpdateDTO.getEmail() + " is already in use.");
            });
            user.setEmail(userUpdateDTO.getEmail());
        }

        // Bước 3: Cập nhật các thông tin khác từ DTO
        user.setFullName(userUpdateDTO.getFullName());
        user.setPhoneNo(userUpdateDTO.getPhoneNo());

        if (userUpdateDTO.getRole() != null) {
            try {
                Set<AppRole> roles = Set.of(AppRole.valueOf(userUpdateDTO.getRole().toUpperCase()));
                user.setRoles(roles);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role specified: " + userUpdateDTO.getRole());
            }
        }

        // Bước 4: Lưu và trả về kết quả
        AppUser updatedUser = appUserRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setActive(false);
        appUserRepository.save(user);
    }

    @Override
    public Page<AppUser> searchUsers(UserSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        return this.appUserRepository.searchAppUserByValue(request.getSearchValue(), pageRequest);
    }

    // Chuyển Entity sang DTO, map cả vehicles
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
                VehicleDto vd = new VehicleDto();
                vd.setLicensePlate(v.getLicensePlate());
                vd.setCarModel(v.getCarModel());
                vd.setVinNumber(v.getVinNumber());
                vd.setNextKm(v.getNextKm());
                vd.setNextDate(v.getNextDate());
                vd.setOldKm(v.getOldKm());
                vd.setOldDate(v.getOldDate());
                return vd;
            }).collect(Collectors.toList());

            dto.setVehicles(vehicleDtos);
        }

        return dto;
    }

    @Override
    public void createUser(UserCreateDTO userCreateDTO) {
        AppUser appUser = new AppUser();
        appUser.setEmail(userCreateDTO.getEmail());
        appUser.setFullName(userCreateDTO.getFullName());
        appUser.setPhoneNo(userCreateDTO.getPhoneNo());
        Set<AppRole> roles = Set.of(AppRole.valueOf(userCreateDTO.getRole()));
        appUser.setRoles(roles);
        this.appUserRepository.save(appUser);
    }

    @Override
    public AppUser getCurrentUser(OidcUser oidcUser) {
        return this.appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<AppUser> getUserListByRole(String role) {
        return this.appUserRepository.findByRoles(AppRole.valueOf(role.toUpperCase()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        AppUser user = appUserRepository.findByEmailWithVehicles(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return convertToDto(user); // map vehicles luôn
    }
}
