package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.service.ServiceRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    // Constructor để Spring tự động inject Service
    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    /**
     * API cho Admin/Staff/Technician tạo một phiếu dịch vụ (bản ghi lịch sử) mới.
     * Yêu cầu quyền ADMIN.
     * @param request Dữ liệu đầu vào chứa bookingId, số km và các hạng mục đã làm.
     * @return Thông tin chi tiết của phiếu dịch vụ vừa được tạo.
     */
    @PostMapping("/admin/service-records")
    public ResponseEntity<ServiceRecordResponseDto> createServiceRecord(@RequestBody CreateServiceRecordRequest request) {
        ServiceRecordResponseDto createdRecord = serviceRecordService.createServiceRecord(request);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    /**
     * API cho người dùng (đã đăng nhập) xem lịch sử bảo dưỡng của một xe theo biển số.
     * @param licensePlate Biển số xe cần tra cứu.
     * @return Danh sách các bản ghi lịch sử, sắp xếp theo ngày gần nhất.
     */
    @GetMapping("/service-records")
    public ResponseEntity<List<ServiceRecordResponseDto>> getServiceHistory(@RequestParam String licensePlate) {
        List<ServiceRecordResponseDto> history = serviceRecordService.getHistoryByLicensePlate(licensePlate);
        return ResponseEntity.ok(history);
    }
}