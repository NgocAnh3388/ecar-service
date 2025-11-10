package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto updateUser(Long id, UserCreateDTO userUpdateDTO);
    void createUser(UserCreateDTO userCreateDTO);
    void deleteUser(Long id);

    // GHI CHÚ: Sửa kiểu trả về từ Page<AppUser> thành Page<UserDto>
    Page<UserDto> searchUsers(UserSearchRequest request);

    AppUser getCurrentUser(OidcUser oidcUser);
    List<AppUser> getUserListByRole(String role);

}
