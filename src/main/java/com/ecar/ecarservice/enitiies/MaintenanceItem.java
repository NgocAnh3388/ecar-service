package com.ecar.ecarservice.enitiies;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_item")
@Getter
@Setter
public class MaintenanceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên hạng mục, VD: "Lọc gió điều hòa"

    @Column(nullable = false)
    private String category; // Loại hạng mục, VD: "HẠNG MỤC BẢO DƯỠNG"
}
