package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.responses.ProfitReportResponse;
import java.time.LocalDateTime;

public interface ReportService {

    /**
     * Lấy báo cáo Lợi nhuận (Doanh thu - Chi phí) theo khoảng thời gian chính xác.
     * @param startDate Ngày và giờ bắt đầu
     * @param endDate Ngày và giờ kết thúc
     * @return Báo cáo lợi nhuận
     */
    ProfitReportResponse getProfitReport(LocalDateTime startDate, LocalDateTime endDate);
}