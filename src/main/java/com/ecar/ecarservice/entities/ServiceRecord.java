package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_records")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết tới booking gốc
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

    // Lưu lại thông tin xe tại thời điểm bảo dưỡng
    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private int kilometerReading; // Số km tại thời điểm bảo dưỡng

    @Column(nullable = false)
    private LocalDateTime serviceDate; // Ngày thực hiện dịch vụ

    // Danh sách các hạng mục chi tiết đã thực hiện
    @OneToMany(mappedBy = "serviceRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRecordDetail> details = new ArrayList<>();

    // Thay thế List<ServiceRecordDetail> cũ bằng mối quan hệ này
    @OneToMany(mappedBy = "serviceRecord", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UsedSparePart> usedParts = new ArrayList<>();
    private List<ServicePartUsage> usedParts = new ArrayList<>();

    // ---  CÁC TRƯỜNG TÀI CHÍNH để lưu tổng chi phí phụ tùng ---
    @Column(precision = 19, scale = 2)
    private BigDecimal totalPartsCost;

    @Column(precision = 19, scale = 2)
    private BigDecimal laborCost;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalActualCost;

    @Column(precision = 19, scale = 2)
    private BigDecimal coveredByPackage;

    @Column(precision = 19, scale = 2)
    private BigDecimal additionalCost;

    // Các trường Auditing
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy; // Staff hoặc Technician đã tạo phiếu này
}
