package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"center_id", "spare_part_id"})) // Đảm bảo mỗi phụ tùng chỉ có 1 bản ghi tồn kho tại 1 center
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center; // Tồn kho tại Center nào?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id", nullable = false)
    private SparePart sparePart; // Của phụ tùng nào?

    @Column(nullable = false)
    private int stockQuantity; // Số lượng tồn kho hiện tại

    @Column(nullable = false)
    private int minStockLevel; // Mức tồn kho tối thiểu cần duy trì
}
