package com.ecar.ecarservice.enitiies;

import com.ecar.ecarservice.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // Kích hoạt tính năng Auditing
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Liên kết với người dùng đã đặt lịch ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    // --- Thông tin liên hệ (lưu lại phòng trường hợp user đổi sđt) ---
    @Column(nullable = false)
    private String customerPhoneNumber;

    // --- Thông tin xe ---
    @Column(nullable = false)
    private String licensePlate; // Biển số xe

    private String carModel;     // Dòng xe
    private String vinNumber;      // Số VIN

    // --- Thông tin lịch hẹn ---
    @Column(nullable = false)
    private String serviceCenter; // Xưởng dịch vụ

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime; // Ngày giờ hẹn

    private String serviceAdvisor; // Cố vấn dịch vụ (có thể null)

    @Lob // Dùng cho các trường văn bản dài
    @Column(columnDefinition = "TEXT")
    private String notes; // Nội dung yêu cầu thêm

    // --- Trạng thái của booking ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    // --- 4 Fields Auditing ---
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;
}
