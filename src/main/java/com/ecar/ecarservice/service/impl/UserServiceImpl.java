package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
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
    public List<UserDto> getAllUsers() {
        return appUserRepository.findAllByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        AppUser user = appUserRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto updateUserRoles(Long id, Set<AppRole> roles) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setRoles(roles);
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
        dto.setRoles(user.getRoles());
        dto.setActive(user.isActive());
        return dto;
    }

}