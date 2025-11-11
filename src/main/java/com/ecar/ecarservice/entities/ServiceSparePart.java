package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_spare_parts_map") // Bảng map giữa Dịch vụ và Phụ tùng
@Getter
@Setter
public class ServiceSparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service; // Dịch vụ nào? (e.g., "Brake Pad Replacement")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id", nullable = false)
    private SparePart sparePart; // Cần phụ tùng nào? (e.g., "VF3 Front Brake Pads")

    // (Tùy chọn) Số lượng mặc định cần dùng cho dịch vụ này
    // Ví dụ: Thay dầu cần 1 lọc dầu, thay bugi cần 4 cái
    @Column(nullable = false, columnDefinition = "int default 1")
    private int defaultQuantity = 1;
}
