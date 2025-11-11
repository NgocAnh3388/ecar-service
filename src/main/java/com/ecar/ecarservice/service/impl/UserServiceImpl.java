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
import com.ecar.ecarservice.repositories.SubscriptionInfoRepository;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import com.ecar.ecarservice.dto.SubscriptionInfoDto;
import com.ecar.ecarservice.dto.MaintenanceHistoryDTO;
import com.ecar.ecarservice.repositories.PaymentHistoryRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final MaintenanceHistoryRepository maintenanceHistoryRepository;
    private final SubscriptionInfoRepository subscriptionInfoRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public UserServiceImpl(AppUserRepository appUserRepository,
                           MaintenanceHistoryRepository maintenanceHistoryRepository,
                           SubscriptionInfoRepository subscriptionInfoRepository,
                           PaymentHistoryRepository paymentHistoryRepository) {
        this.appUserRepository = appUserRepository;
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
        this.subscriptionInfoRepository = subscriptionInfoRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    // ===================== THEM MOI (giữ tu file 2) =====================
    @Override
    public AppUser getCurrentUserById(Long id) {
        return appUserRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // ===================== LAY TAT CA NGUOI DUNG =====================
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return appUserRepository.findAllByActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ===================== LAY USER THEO ID =====================
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        AppUser user = appUserRepository.findByIdWithVehicles(id)
                .orElseThrow(() -> new EntityNotFoundException("Active user not found with id: " + id));

        UserDto dto = convertToDto(user);

        // 🔹 Lấy lịch sử bảo dưỡng (chuyển sang DTO)
        List<MaintenanceHistoryDTO> histories = maintenanceHistoryRepository
                .searchByOwner(id, "", PageRequest.of(0, 100))
                .getContent()
                .stream()
                .map(mh -> MaintenanceHistoryDTO.builder()
                        .carName(mh.getVehicle() != null && mh.getVehicle().getCarModel() != null
                                ? mh.getVehicle().getCarModel().getCarName() : null)
                        .carType(mh.getVehicle() != null && mh.getVehicle().getCarModel() != null
                                ? mh.getVehicle().getCarModel().getCarType() : null)
                        .licensePlate(mh.getVehicle() != null ? mh.getVehicle().getLicensePlate() : null)
                        .submittedAt(mh.getSubmittedAt())
                        .completedAt(mh.getCompletedAt())
                        .status(mh.getStatus())
                        .build()
                ).toList();
        dto.setMaintenanceHistories(histories);

        // 🔹 Lấy gói dịch vụ (chuyển sang DTO)
        List<SubscriptionInfoDto> subscriptions = subscriptionInfoRepository.findByOwnerId(id)
                .stream()
                .map(sub -> {
                    SubscriptionInfoDto sid = new SubscriptionInfoDto();
                    sid.setId(sub.getId());
                    sid.setStartDate(sub.getStartDate());
                    sid.setEndDate(sub.getEndDate());
                    sid.setPaymentDate(sub.getPaymentDate());
                    return sid;
                }).toList();
        dto.setSubscriptionInfos(subscriptions);

        return dto;
    }


    // ===================== CAP NHAT THONG TIN USER =====================
    @Override
    @Transactional
    public UserDto updateUser(Long id, UserCreateDTO userUpdateDTO) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        String currentEmail = user.getEmail();
        String newEmail = userUpdateDTO.getEmail();

        if (currentEmail != null && !currentEmail.equalsIgnoreCase(newEmail)) {
            appUserRepository.findByEmail(newEmail).ifPresent(existingUser -> {
                throw new IllegalStateException("Email " + newEmail + " is already in use.");
            });
            user.setEmail(newEmail);
        }

        user.setFullName(userUpdateDTO.getFullName());
        user.setPhoneNo(userUpdateDTO.getPhoneNo());

        if (userUpdateDTO.getRole() != null) {
            try {
                Set<AppRole> roles = new HashSet<>();
                roles.add(AppRole.valueOf(userUpdateDTO.getRole().toUpperCase()));
                user.setRoles(roles);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role specified: " + userUpdateDTO.getRole());
            }
        }

        try {
            AppUser updatedUser = appUserRepository.save(user);
            return convertToDto(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // ===================== XOA USER =====================
    @Override
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setActive(false);
        appUserRepository.save(user);
    }

    // ===================== CHUYEN TRANG THAI ACTIVE =====================
    @Override
    @Transactional
    public void toggleActiveUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.setActive(!user.isActive());
        appUserRepository.save(user);
    }

    // ===================== TIM KIEM USER =====================
    @Override
    @Transactional(readOnly = true)
    public Page<AppUser> searchUsers(UserSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        Page<Long> idsPage = this.appUserRepository.searchUserIdsByValue(request.getSearchValue(), pageRequest);
        if (!idsPage.hasContent()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        List<Long> userIds = idsPage.getContent();
        List<AppUser> usersWithDetails = this.appUserRepository.findAllWithDetailsByIds(userIds);

        Map<Long, AppUser> userMap = usersWithDetails.stream()
                .collect(Collectors.toMap(AppUser::getId, Function.identity()));

        List<AppUser> sortedUsers = userIds.stream()
                .map(userMap::get)
                .collect(Collectors.toList());

        return new PageImpl<>(sortedUsers, pageRequest, idsPage.getTotalElements());
    }

    // ===================== CHUYEN ENTITY -> DTO =====================
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

    // ===================== TAO USER MOI =====================
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

        Set<AppRole> roles = new HashSet<>();
        roles.add(AppRole.valueOf(userCreateDTO.getRole().toUpperCase()));
        appUser.setRoles(roles);

        this.appUserRepository.save(appUser);
    }

    // ===================== LAY USER HIEN TAI (OIDC) =====================
    @Override
    public AppUser getCurrentUser(OidcUser oidcUser) {
        return this.appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ===================== LAY DANH SACH USER THEO ROLE =====================
    @Override
    public List<AppUser> getUserListByRole(String role) {
        return this.appUserRepository.findByRoles(AppRole.valueOf(role.toUpperCase()));
    }

    // ===================== LAY USER THEO EMAIL =====================
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        AppUser user = appUserRepository.findByEmailWithVehicles(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return convertToDto(user);
    }
}
