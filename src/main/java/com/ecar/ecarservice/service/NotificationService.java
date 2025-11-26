package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Notification;
import com.ecar.ecarservice.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // Hàm dùng chung để tạo thông báo
    public void createNotification(AppUser recipient, String title, String message, String type) {
        if (recipient == null) return;

        Notification noti = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(noti);
    }

    // Lấy danh sách cho Controller
    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    // Đánh dấu đã đọc
    public void markAsRead(Long notiId) {
        notificationRepository.findById(notiId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    // Đánh dấu tất cả là đã đọc
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }
}