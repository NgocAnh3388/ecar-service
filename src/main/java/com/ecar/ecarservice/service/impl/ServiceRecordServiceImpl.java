package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceDetailDto; // <<-- DÒNG BẠN ĐANG THIẾU
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.enitiies.ServiceRecord;
import com.ecar.ecarservice.enitiies.ServiceRecordDetail;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.repositories.BookingRepository;
import com.ecar.ecarservice.repositories.ServiceRecordRepository;
import com.ecar.ecarservice.service.ServiceRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository recordRepository;
    private final BookingRepository bookingRepository;

    // Constructor để Spring tự động inject các Repository cần thiết
    public ServiceRecordServiceImpl(ServiceRecordRepository recordRepository, BookingRepository bookingRepository) {
        this.recordRepository = recordRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request) {
        // 1. Tìm booking gốc từ ID
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + request.getBookingId()));

        // (Thêm) Kiểm tra xem booking đã được xử lý chưa
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("This booking has already been completed.");
        }

        // 2. Tạo bản ghi lịch sử chính
        ServiceRecord record = new ServiceRecord();
        record.setBooking(booking);
        record.setLicensePlate(booking.getLicensePlate());
        record.setKilometerReading(request.getKilometerReading());
        record.setServiceDate(LocalDateTime.now()); // Lấy thời gian hiện tại

        // 3. Tạo các bản ghi chi tiết từ DTO
        List<ServiceRecordDetail> details = request.getDetails().stream().map(dto -> {
            ServiceRecordDetail detail = new ServiceRecordDetail();
            detail.setItemName(dto.getItemName());
            detail.setAction(dto.getAction());
            detail.setNotes(dto.getNotes());
            detail.setServiceRecord(record); // Thiết lập mối quan hệ 2 chiều
            return detail;
        }).collect(Collectors.toList());

        record.setDetails(details);

        // 4. Cập nhật trạng thái booking thành "Hoàn thành"
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // 5. Lưu bản ghi lịch sử (và các chi tiết của nó sẽ được tự động lưu nhờ CascadeType.ALL)
        ServiceRecord savedRecord = recordRepository.save(record);

        // 6. Chuyển đổi sang DTO để trả về cho client
        return convertToDto(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate) {
        return recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Hàm tiện ích để chuyển đổi từ Entity ServiceRecord sang DTO ServiceRecordResponseDto
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
