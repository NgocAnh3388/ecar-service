// Tạo một file mới, ví dụ: TechnicianController.java
package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.service.TechnicianAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianAssignmentService technicianAssignmentService;

    /**
     * API MỚI: Lấy danh sách Kỹ thuật viên CÓ SẴN (không quá tải)
     * dựa theo ID của trung tâm (centerId).
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<List<AppUser>> getAvailableTechnicians(@RequestParam Long centerId) {

        List<AppUser> availableTechnicians = technicianAssignmentService
                .getAvailableTechniciansByCenter(centerId);

        return ResponseEntity.ok(availableTechnicians);
    }
}