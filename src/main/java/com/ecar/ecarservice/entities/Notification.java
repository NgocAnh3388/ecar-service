package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;

    private boolean isRead; // Đã đọc chưa

    private LocalDateTime createdAt;

    // Người nhận thông báo
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser recipient;

    // Loại thông báo (Optional): BOOKING, PAYMENT, SYSTEM...
    private String type;
}