package com.ecar.ecarservice.entities;

import com.ecar.ecarservice.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description; // Mô tả chi phí

    @Column(nullable = false)
    private BigDecimal amount; // Số tiền chi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category; // Phân loại chi phí

    @Column(nullable = false)
    private LocalDate expenseDate; // Ngày chi (dùng LocalDate là đủ)

    // @Column(name = "center_id") // Tùy chọn nếu quản lý nhiều trung tâm
    // private Long centerId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy; // Lưu email/username của Admin tạo chi phí
}