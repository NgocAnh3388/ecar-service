package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        return appUserRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        AppUser user = appUserRepository.findByIdWithVehicles(id)
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserCreateDTO userUpdateDTO) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!user.getEmail().equalsIgnoreCase(userUpdateDTO.getEmail())) {
            appUserRepository.findByEmail(userUpdateDTO.getEmail()).ifPresent(existingUser -> {
                throw new IllegalStateException("Email " + userUpdateDTO.getEmail() + " is already in use.");
            });
            user.setEmail(userUpdateDTO.getEmail());
        }

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

        AppUser updatedUser = appUserRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setActive(false);
        appUserRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppUser> searchUsers(UserSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        // Bước 1: Chỉ lấy về ID và thông tin phân trang
        Page<Long> idsPage = this.appUserRepository.searchUserIdsByValue(request.getSearchValue(), pageRequest);

        if (!idsPage.hasContent()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        List<Long> userIds = idsPage.getContent();

        // Bước 2: Lấy đầy đủ thông tin cho các ID đã tìm thấy trong 1 query duy nhất
        List<AppUser> usersWithDetails = this.appUserRepository.findAllWithDetailsByIds(userIds);

        // Sắp xếp lại danh sách usersWithDetails theo thứ tự của userIds để đảm bảo phân trang đúng
        Map<Long, AppUser> userMap = usersWithDetails.stream()
                .collect(Collectors.toMap(AppUser::getId, Function.identity()));

        List<AppUser> sortedUsers = userIds.stream()
                .map(userMap::get)
                .collect(Collectors.toList());

        // Trả về một Page mới với dữ liệu đã được tải đầy đủ
        return new PageImpl<>(sortedUsers, pageRequest, idsPage.getTotalElements());
    }

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
    @Transactional
    public void createUser(UserCreateDTO userCreateDTO) {
        appUserRepository.findByEmail(userCreateDTO.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email already exists");
        });
        AppUser appUser = new AppUser();
        appUser.setEmail(userCreateDTO.getEmail());
        appUser.setFullName(userCreateDTO.getFullName());
        appUser.setPhoneNo(userCreateDTO.getPhoneNo());
        Set<AppRole> roles = Set.of(AppRole.valueOf(userCreateDTO.getRole().toUpperCase()));
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
        return convertToDto(user);
    }
}