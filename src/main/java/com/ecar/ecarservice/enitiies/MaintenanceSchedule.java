package com.ecar.ecarservice.enitiies;

import com.ecar.ecarservice.enums.MaintenanceAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_schedule")
@Getter
@Setter
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MaintenanceItem item;

    @Column(nullable = false)
    private int kilometerMark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceAction action;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

}