package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
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
        return appUserRepository.findAllByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        AppUser user = appUserRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto updateUser(UserCreateDTO userCreateDTO) {
        AppUser user = appUserRepository.findByEmail(userCreateDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userCreateDTO.getEmail()));
        user.setFullName(userCreateDTO.getFullName());
        Set<AppRole> roles = Set.of(AppRole.valueOf(userCreateDTO.getRole()));
        user.setRoles(roles);
        user.setPhoneNo(userCreateDTO.getPhoneNo());
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

    // Hàm tiện ích để chuyển đổi Entity sang DTO
    private UserDto convertToDto(AppUser user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());

        // 👇 Map danh sách xe sang VehicleDto
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
}
