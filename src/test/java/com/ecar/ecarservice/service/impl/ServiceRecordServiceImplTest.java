package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceDetailDto;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
import com.ecar.ecarservice.entities.Booking;
import com.ecar.ecarservice.entities.ServiceRecord;
import com.ecar.ecarservice.entities.ServiceRecordDetail;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.enums.MaintenanceAction;
import com.ecar.ecarservice.repositories.BookingRepository;
import com.ecar.ecarservice.repositories.ServiceRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRecordServiceImplTest {

    @Mock
    private ServiceRecordRepository recordRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ServiceRecordServiceImpl serviceRecordService;

    // =============================================
    //         HAPPYCASE
    // =============================================

    @Test
    @DisplayName("createServiceRecord_Success: Should create record and complete the original booking")
    void testCreateServiceRecord_Success() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        existingBooking.setLicensePlate("77A-77777");
        existingBooking.setStatus(BookingStatus.CONFIRMED); // Booking đang ở trạng thái hợp lệ

        CreateServiceRecordRequest request = new CreateServiceRecordRequest();
        request.setBookingId(bookingId);
        request.setKilometerReading(50000);

        ServiceDetailDto detailDto = new ServiceDetailDto();
        detailDto.setItemName("Oil Change");
        detailDto.setAction(MaintenanceAction.REPLACE);
        request.setDetails(List.of(detailDto));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        // Giả lập hàm save trả về chính đối tượng được truyền vào
        when(recordRepository.save(any(ServiceRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        serviceRecordService.createServiceRecord(request);

        // Assert
        // 1. Kiểm tra booking có được cập nhật thành COMPLETED không
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(1)).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals(BookingStatus.COMPLETED, savedBooking.getStatus());

        // 2. Kiểm tra ServiceRecord có được tạo đúng thông tin không
        ArgumentCaptor<ServiceRecord> recordCaptor = ArgumentCaptor.forClass(ServiceRecord.class);
        verify(recordRepository, times(1)).save(recordCaptor.capture());
        ServiceRecord savedRecord = recordCaptor.getValue();

        assertEquals(bookingId, savedRecord.getBooking().getId());
        assertEquals(50000, savedRecord.getKilometerReading());
        assertEquals(1, savedRecord.getDetails().size());
        assertEquals("Oil Change", savedRecord.getDetails().get(0).getItemName());
        assertEquals(MaintenanceAction.REPLACE, savedRecord.getDetails().get(0).getAction());
    }

    @Test
    @DisplayName("getHistoryByLicensePlate: Should return list of service records for a license plate")
    void testGetHistoryByLicensePlate() {
        // Arrange
        String licensePlate = "12A-345.67";
        ServiceRecord record1 = new ServiceRecord();
        record1.setId(1L);
        record1.setLicensePlate(licensePlate);
        record1.setDetails(Collections.emptyList()); // Khởi tạo list rỗng để tránh NullPointerException

        when(recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate)).thenReturn(List.of(record1));

        // Act
        List<ServiceRecordResponseDto> result = serviceRecordService.getHistoryByLicensePlate(licensePlate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(licensePlate, result.get(0).getLicensePlate());
        verify(recordRepository, times(1)).findByLicensePlateOrderByServiceDateDesc(licensePlate);
    }

    // =============================================
    //         UNHAPPYCASE
    // =============================================

    @Test
    @DisplayName("createServiceRecord_BookingAlreadyCompleted: Should throw IllegalStateException")
    void testCreateServiceRecord_BookingAlreadyCompleted_ThrowsException() {
        // Arrange
        Long bookingId = 1L;
        Booking completedBooking = new Booking();
        completedBooking.setId(bookingId);
        completedBooking.setStatus(BookingStatus.COMPLETED); // Booking đã hoàn thành

        CreateServiceRecordRequest request = new CreateServiceRecordRequest();
        request.setBookingId(bookingId);
        // Cần set details để không bị NullPointer
        request.setDetails(List.of(new ServiceDetailDto()));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(completedBooking));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            serviceRecordService.createServiceRecord(request);
        });

        assertEquals("This booking has already been completed.", exception.getMessage());
        verify(recordRepository, never()).save(any()); // Hàm save của recordRepository không bao giờ được gọi
    }

    @Test
    @DisplayName("createServiceRecord_BookingNotFound: Should throw EntityNotFoundException")
    void testCreateServiceRecord_BookingNotFound_ThrowsException() {
        // Arrange
        Long nonExistentBookingId = 99L;
        CreateServiceRecordRequest request = new CreateServiceRecordRequest();
        request.setBookingId(nonExistentBookingId);
        request.setKilometerReading(50000);
        request.setDetails(List.of(new ServiceDetailDto()));

        // Giả lập việc không tìm thấy booking
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            serviceRecordService.createServiceRecord(request);
        });

        assertEquals("Booking not found with id: " + nonExistentBookingId, exception.getMessage());

        // Đảm bảo không có bản ghi nào được lưu
        verify(recordRepository, never()).save(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("getHistoryByLicensePlate_ReturnsEmptyList_WhenNoRecordsFound")
    void testGetHistoryByLicensePlate_ReturnsEmptyList() {
        // Arrange
        String licensePlate = "NON-EXISTENT-PLATE";
        when(recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate)).thenReturn(List.of());

        // Act
        List<ServiceRecordResponseDto> result = serviceRecordService.getHistoryByLicensePlate(licensePlate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}