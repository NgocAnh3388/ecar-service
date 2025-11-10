package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceDetailDto;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.entities.Booking;
import com.ecar.ecarservice.entities.ServiceRecord;
import com.ecar.ecarservice.entities.ServiceRecordDetail;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.enums.MaintenanceStatus;
import com.ecar.ecarservice.repositories.BookingRepository;
import com.ecar.ecarservice.repositories.MaintenanceHistoryRepository;
import com.ecar.ecarservice.repositories.ServiceRecordRepository;
import com.ecar.ecarservice.service.MaintenanceService;
import com.ecar.ecarservice.service.ServiceRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ghi chú: Class này chịu trách nhiệm tạo ra "Biên bản Dịch vụ" (Service Record),
 * đây là bản ghi lịch sử cuối cùng cho khách hàng xem.
 * Nó cũng có nhiệm vụ quan trọng là kết thúc quy trình làm việc nội bộ (MaintenanceHistory).
 */
@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository recordRepository;
    private final BookingRepository bookingRepository;
    private final MaintenanceHistoryRepository maintenanceHistoryRepository;
    private final MaintenanceService maintenanceService;

    public ServiceRecordServiceImpl(ServiceRecordRepository recordRepository,
                                    BookingRepository bookingRepository,
                                    MaintenanceHistoryRepository maintenanceHistoryRepository,
                                    MaintenanceService maintenanceService) {
        this.recordRepository = recordRepository;
        this.bookingRepository = bookingRepository;
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
        this.maintenanceService = maintenanceService;
    }

//     Tạo một biên bản dịch vụ mới từ một booking đã có.
//     Đồng thời, tìm và đóng phiếu công việc nội bộ tương ứng.
    @Override
    @Transactional
    public ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request) {
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Service details cannot be empty.");
        }

        // 1. Tìm booking gốc từ ID
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + request.getBookingId()));

        // 2. Kiểm tra xem booking đã được xử lý chưa để tránh tạo trùng lặp
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("This booking has been completed before.");
        }

        // 3. Tạo bản ghi lịch sử chính (ServiceRecord)
        ServiceRecord record = new ServiceRecord();
        record.setBooking(booking);
        record.setLicensePlate(booking.getLicensePlate());
        record.setKilometerReading(request.getKilometerReading());
        record.setServiceDate(LocalDateTime.now());

        // 4. Tạo các bản ghi chi tiết từ DTO và liên kết chúng với bản ghi chính
        List<ServiceRecordDetail> details = request.getDetails().stream().map(dto -> {
            ServiceRecordDetail detail = new ServiceRecordDetail();
            detail.setItemName(dto.getItemName());
            detail.setAction(dto.getAction());
            detail.setNotes(dto.getNotes());
            detail.setServiceRecord(record); // Thiết lập mối quan hệ 2 chiều
            return detail;
        }).collect(Collectors.toList());
        record.setDetails(details);

        // 5. Cập nhật trạng thái của booking gốc thành "Hoàn thành"
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // 6. Lưu bản ghi lịch sử (và các chi tiết của nó sẽ tự động được lưu nhờ CascadeType.ALL)
        ServiceRecord savedRecord = recordRepository.save(record);

        // 7. KẾT NỐI WORKFLOW: Tìm phiếu công việc nội bộ (MaintenanceHistory) đang hoạt động của xe này
        // và gọi service để chuyển nó sang trạng thái cuối cùng là "DONE".
        maintenanceHistoryRepository
                .findFirstByVehicleLicensePlateAndStatusNotIn(booking.getLicensePlate(), List.of(MaintenanceStatus.DONE))
                .ifPresent(ticket -> maintenanceService.finalizeAndHandOver(ticket.getId()));

        // 8. Chuyển đổi sang DTO để trả về cho client
        return convertToDto(savedRecord);
    }

//      Lấy lịch sử bảo dưỡng của một xe theo biển số.
    @Override
    @Transactional(readOnly = true)
    public List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate) {
        return recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

//      Hàm tiện ích để chuyển đổi từ Entity sang DTO.
    private ServiceRecordResponseDto convertToDto(ServiceRecord record) {
        ServiceRecordResponseDto recordDto = new ServiceRecordResponseDto();
        recordDto.setId(record.getId());
        recordDto.setLicensePlate(record.getLicensePlate());
        recordDto.setKilometerReading(record.getKilometerReading());
        recordDto.setServiceDate(record.getServiceDate());
        recordDto.setCreatedBy(record.getCreatedBy());

        // Chuyển đổi danh sách chi tiết
        List<ServiceDetailDto> detailDtos = record.getDetails().stream().map(detail -> {
            ServiceDetailDto detailDto = new ServiceDetailDto();
            detailDto.setItemName(detail.getItemName());
            detailDto.setAction(detail.getAction());
            detailDto.setNotes(detail.getNotes());
            return detailDto;
        }).collect(Collectors.toList());

        recordDto.setDetails(detailDtos);

        return recordDto;
    }
}