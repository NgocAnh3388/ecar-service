package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Notification;
import com.ecar.ecarservice.service.NotificationService;
import com.ecar.ecarservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService; // Để lấy current user

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(@AuthenticationPrincipal OidcUser user) {
        AppUser currentUser = userService.getCurrentUser(user); // Hàm lấy user từ token
        return ResponseEntity.ok(notificationService.getMyNotifications(currentUser.getId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal OidcUser user) {
        AppUser currentUser = userService.getCurrentUser(user);
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok().build();
    }
}