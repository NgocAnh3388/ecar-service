package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUserRoles(Long id, Set<AppRole> roles);
    void deleteUser(Long id);
    Page<AppUser> searchUsers(UserSearchRequest request);
}