package com.ecar.ecarservice.enitiies;

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
@ToString
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MaintenanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "owner_id")
    private Long owner_id;

    @Column(name = "staff_id")
    private Long staff_id;

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(name = "num_of_km")
    private Long numOfKm;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "technician_receive_at")
    private LocalDateTime technicianReceivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "status")
    private String status;

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

    @Column(name = "center_id")
    private Long centerId;

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