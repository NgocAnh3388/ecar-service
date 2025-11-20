package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import com.ecar.ecarservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

public class ReportController {

    private final ReportService reportService;

    /**
     * API để Admin xem báo cáo LỢI NHUẬN (Doanh thu - Chi phí)
     *
     * @param startDate Ngày bắt đầu (format: YYYY-MM-DD)
     * @param endDate Ngày kết thúc (format: YYYY-MM-DD)
     * @return Báo cáo Lợi nhuận
     */
    @GetMapping("/profit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfitReportResponse> getProfitReport(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        ProfitReportResponse response = reportService.getProfitReport(startDateTime, endDateTime);
        return ResponseEntity.ok(response);
    }

}