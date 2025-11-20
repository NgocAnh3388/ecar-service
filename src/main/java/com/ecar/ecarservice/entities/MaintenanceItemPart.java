package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Entity mới để lưu phụ tùng đã dùng cho một phiếu dịch vụ
//Ghi nhận Phụ tùng DỰ KIẾN sẽ dùng
@Entity
@Table(name = "maintenance_item_parts")
@Getter
@Setter
public class MaintenanceItemPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_history_id")
    private MaintenanceHistory maintenanceHistory; // Thuộc phiếu dịch vụ nào

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart; // Phụ tùng nào

    @Column(name = "quantity")
    private int quantity; // Số lượng
}