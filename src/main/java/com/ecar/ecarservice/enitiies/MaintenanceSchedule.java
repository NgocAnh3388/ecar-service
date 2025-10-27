package com.ecar.ecarservice.enitiies;

import com.ecar.ecarservice.enums.MaintenanceAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_schedule")
@Getter
@Setter
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MaintenanceItem item; // Liên kết tới hạng mục

    @Column(nullable = false)
    private int kilometerMark; // Mốc kilomet, VD: 12000, 24000

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceAction action; // Hành động: "INSPECT" (kiểm tra) hoặc "REPLACE" (thay mới)
}