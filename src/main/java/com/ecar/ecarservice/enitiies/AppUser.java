package com.ecar.ecarservice.enitiies;

import com.ecar.ecarservice.enums.AppRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sub", unique = true)
    private String sub;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_no")
    private String phoneNo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<AppRole> roles = new HashSet<>();

    // Mặc định, tất cả user khi được tạo ra sẽ có active = true
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Vehicle> vehicles;

}
