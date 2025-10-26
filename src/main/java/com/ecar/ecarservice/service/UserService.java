package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enums.AppRole;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUserRoles(Long id, Set<AppRole> roles);
    void deleteUser(Long id);

}