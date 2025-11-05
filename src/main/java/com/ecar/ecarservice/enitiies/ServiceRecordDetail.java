package com.ecar.ecarservice.enitiies;
import com.ecar.ecarservice.enums.MaintenanceAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_record_details")
@Getter
@Setter
public class ServiceRecordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_record_id", nullable = false)
    private ServiceRecord serviceRecord;

    @Column(nullable = false)
    private String itemName; // Tên hạng mục, ví dụ: "Dầu phanh"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceAction action; // Hành động đã làm: INSPECT hoặc REPLACE

    private String notes; // Ghi chú của kỹ thuật viên
}
