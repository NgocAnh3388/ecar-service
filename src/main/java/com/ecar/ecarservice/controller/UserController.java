package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.UserCreateDTO;
import com.ecar.ecarservice.dto.UserDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.payload.requests.UserSearchRequest;
import com.ecar.ecarservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserCreateDTO userCreateDTO) {
        return ResponseEntity.ok(userService.updateUser(userCreateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
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

}