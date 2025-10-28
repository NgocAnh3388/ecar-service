package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Get List of Users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 2. Get User with ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 3. Update User (ví dụ: chỉ cập nhật role)
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable Long id, @RequestBody Set<AppRole> roles) {
        return ResponseEntity.ok(userService.updateUserRoles(id, roles));
    }

    // 4. Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

}