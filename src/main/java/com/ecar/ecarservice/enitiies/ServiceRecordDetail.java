package com.ecar.ecarservice.enitiies;
import com.ecar.ecarservice.enums.MaintenanceAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceAction action;

    private String notes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

}