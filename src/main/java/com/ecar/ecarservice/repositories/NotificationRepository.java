package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy thông báo của user, sắp xếp mới nhất trước
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long userId);

    // Đếm số thông báo chưa đọc
    long countByRecipientIdAndIsReadFalse(Long userId);
}