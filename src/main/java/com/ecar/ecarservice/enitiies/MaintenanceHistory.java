package com.ecar.ecarservice.enitiies;

import com.ecar.ecarservice.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "maintenance_history")
@Getter
@Setter
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MaintenanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private AppUser staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private AppUser technician;

    @Column(name = "num_of_km")
    private Long numOfKm;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "technician_receive_at")
    private LocalDateTime technicianReceivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "status")
    private MaintenanceStatus status;

    @Column(name = "is_maintenance")
    private Boolean isMaintenance;

    @Column(name = "is_repair")
    private Boolean isRepair;

    @Column(name = "remark")
    private String remark;

    @Column(name = "staff_receive_at")
    private LocalDateTime staffReceiveAt;

    @Column(name = "hand_over_at")
    private LocalDateTime handOverAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Column(name = "schedule_time")
    private LocalTime scheduleTime;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

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
