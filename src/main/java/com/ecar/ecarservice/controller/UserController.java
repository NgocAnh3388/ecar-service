package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
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

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<AppUser> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(userService.getCurrentUser(oidcUser));
    }

    // 3. Update User (ví dụ: chỉ cập nhật role)
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserCreateDTO userCreateDTO) {
        return ResponseEntity.ok(userService.updateUser(userCreateDTO));
    }

    // 4. Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        this.userService.createUser(userCreateDTO);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AppUser>> searchUsers(@RequestBody UserSearchRequest request) {
        Page<AppUser> searchResult = userService.searchUsers(request);
        return new  ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @RequestMapping(value = "get-by-role/{roleName}", method = RequestMethod.GET)
    public ResponseEntity<List<AppUser>> getUserListByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(this.userService.getUserListByRole(roleName));
    }

}
