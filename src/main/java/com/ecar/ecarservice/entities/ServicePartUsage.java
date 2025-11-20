package com.ecar.ecarservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
//Ghi nhận Phụ tùng ĐÃ DÙNG trong hóa đơn
@Entity
@Table(name = "service_part_usage")
@Getter
@Setter
public class ServicePartUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_record_id", nullable = false)
    private ServiceRecord serviceRecord; // Liên kết tới hóa đơn dịch vụ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id", nullable = false)
    private SparePart sparePart; // Phụ tùng nào đã được dùng

    @Column(nullable = false)
    private int quantityUsed; // Số lượng đã dùng

    @Column(nullable = false)
    private BigDecimal priceAtTimeOfUse; // Giá tại thời điểm sử dụng
}
