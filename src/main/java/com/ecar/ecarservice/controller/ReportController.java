package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import com.ecar.ecarservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
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
    @PreAuthorize("hasRole('ADMIN')") // <-- BẢO MẬT: CHỈ ADMIN
    public ResponseEntity<ProfitReportResponse> getProfitReport(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        ProfitReportResponse response = reportService.getProfitReport(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}