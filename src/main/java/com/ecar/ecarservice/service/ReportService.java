package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * Lấy báo cáo Lợi nhuận (Doanh thu - Chi phí) theo khoảng ngày.
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Báo cáo lợi nhuận
     */
    ProfitReportResponse getProfitReport(LocalDate startDate, LocalDate endDate);
}