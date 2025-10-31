package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(UserCreateDTO userCreateDTO);
    void createUser(UserCreateDTO userCreateDTO);
    void deleteUser(Long id);
    Page<AppUser> searchUsers(UserSearchRequest request);
    AppUser getCurrentUser(OidcUser oidcUser);
    List<AppUser> getUserListByRole(String role);

}
