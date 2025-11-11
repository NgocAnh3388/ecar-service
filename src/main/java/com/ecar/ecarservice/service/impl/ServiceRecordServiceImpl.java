// src/main/java/com/ecar/ecarservice/service/impl/ServiceRecordServiceImpl.java
package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.*;
import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.repositories.*;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.ServiceRecordService;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service này chịu trách nhiệm cho các nghiệp vụ liên quan đến việc TẠO và TRUY XUẤT
 * các Hóa đơn Dịch vụ (Service Records). Đây là bước cuối cùng và quan trọng nhất trong một quy trình bảo dưỡng,
 * nơi các tính toán tài chính và cập nhật tồn kho được thực hiện.
 */
@Service
@RequiredArgsConstructor
public class ServiceRecordServiceImpl implements ServiceRecordService {

    // Inject các Repository và Service cần thiết cho nghiệp vụ
    private final ServiceRecordRepository recordRepository;
    private final BookingRepository bookingRepository;
    private final SparePartRepository sparePartRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final EmailService emailService;

    /**
     * Phương thức cốt lõi: Tạo một hóa đơn dịch vụ mới.
     * Toàn bộ phương thức được bọc trong một @Transactional để đảm bảo an toàn dữ liệu:
     * hoặc tất cả các bước (trừ kho, tạo hóa đơn, cập nhật booking) đều thành công,
     * hoặc tất cả sẽ được hoàn tác (rollback) nếu có lỗi xảy ra.
     */
    @Override
    @Transactional
    public ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request) {
        // --- Tải và xác thực Booking gốc ---
        // Sử dụng phương thức `findByIdWithCenter` để tải kèm (JOIN FETCH) thông tin của Center,
        // giúp tránh lỗi LazyInitializationException khi gọi booking.getCenter() sau này.
        Booking booking = bookingRepository.findByIdWithCenter(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + request.getBookingId()));

        // Ngăn chặn việc tạo hóa đơn trùng lặp cho một booking đã hoàn thành.
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("This booking has already been completed.");
        }

        // Lấy thông tin Center từ booking, đây là chìa khóa để biết cần trừ kho ở đâu.
        Center serviceCenter = booking.getCenter();

        // --- Chuẩn bị tạo bản ghi ServiceRecord chính ---
        ServiceRecord record = new ServiceRecord();
        record.setBooking(booking);
        record.setLicensePlate(booking.getLicensePlate());
        record.setKilometerReading(request.getKilometerReading());
        record.setServiceDate(LocalDateTime.now());

        // --- Xử lý checklist các công việc đã thực hiện ---
        processServiceDetails(request, record);

        // --- Xử lý phụ tùng đã dùng, trừ tồn kho và gửi cảnh báo ---
        BigDecimal totalPartsCost = processUsedPartsAndDeductStock(request, record, serviceCenter);
        record.setTotalPartsCost(totalPartsCost);

        // --- Tính toán các chi phí còn lại (tiền công, chi phí phát sinh) ---
        calculateAdditionalCosts(request, record);

        // --- Hoàn tất quy trình ---
        // Cập nhật trạng thái của booking gốc thành HOÀN THÀNH.
        booking.setStatus(BookingStatus.COMPLETED);

        // Lưu bản ghi ServiceRecord vào DB.
        // Do có `cascade = CascadeType.ALL` trong Entity, việc lưu `record`
        // sẽ tự động lưu cả các `ServiceRecordDetail` và `ServicePartUsage` liên quan.
        ServiceRecord savedRecord = recordRepository.save(record);

        // Chuyển đổi Entity đã lưu thành DTO để trả về thông tin đầy đủ cho client.
        return convertToDto(savedRecord);
    }

    /**
     * Phương thức private để xử lý danh sách các công việc đã thực hiện từ request.
     */
    private void processServiceDetails(CreateServiceRecordRequest request, ServiceRecord record) {
        if (request.getServiceDetails() != null) {
            List<ServiceRecordDetail> details = request.getServiceDetails().stream().map(dto -> {
                ServiceRecordDetail detail = new ServiceRecordDetail();
                detail.setItemName(dto.getItemName());
                detail.setAction(dto.getAction());
                detail.setNotes(dto.getNotes());
                detail.setServiceRecord(record); // Thiết lập mối quan hệ hai chiều
                return detail;
            }).collect(Collectors.toList());
            record.setDetails(details);
        }
    }

    /**
     * Phương thức private để xử lý danh sách phụ tùng đã dùng, thực hiện trừ kho và tính tổng chi phí.
     * Đây là phần logic quan trọng nhất.
     */
    private BigDecimal processUsedPartsAndDeductStock(CreateServiceRecordRequest request, ServiceRecord record, Center serviceCenter) {
        BigDecimal totalCost = BigDecimal.ZERO;
        if (request.getUsedParts() != null && !request.getUsedParts().isEmpty()) {
            for (UsedPartDto partDto : request.getUsedParts()) {
                if (partDto.quantity() <= 0) continue; // Bỏ qua các dòng có số lượng không hợp lệ.

                // Tìm thông tin chung của phụ tùng
                SparePart sparePart = sparePartRepository.findById(partDto.partId())
                        .orElseThrow(() -> new EntityNotFoundException("Spare part not found with id: " + partDto.partId()));

                // Tìm bản ghi tồn kho của phụ tùng này TẠI ĐÚNG CENTER đang làm dịch vụ
                Inventory inventoryItem = inventoryRepository.findByCenterIdAndSparePartId(serviceCenter.getId(), sparePart.getId())
                        .orElseThrow(() -> new IllegalStateException("Part '" + sparePart.getPartName() + "' is not stocked at center '" + serviceCenter.getCenterName() + "'"));

                // Kiểm tra xem số lượng tồn có đủ không
                if (inventoryItem.getStockQuantity() < partDto.quantity()) {
                    throw new IllegalStateException("Not enough stock for '" + sparePart.getPartName() + "' at '" + serviceCenter.getCenterName() + "'. Required: " + partDto.quantity() + ", Available: " + inventoryItem.getStockQuantity());
                }

                // THỰC HIỆN TRỪ KHO
                inventoryItem.setStockQuantity(inventoryItem.getStockQuantity() - partDto.quantity());

                // Tạo một bản ghi `ServicePartUsage` để ghi lại việc sử dụng này vào hóa đơn
                ServicePartUsage usageRecord = new ServicePartUsage();
                usageRecord.setServiceRecord(record);
                usageRecord.setSparePart(sparePart);
                usageRecord.setQuantityUsed(partDto.quantity());
                usageRecord.setPriceAtTimeOfUse(BigDecimal.valueOf(sparePart.getUnitPrice())); // Ghi lại giá tại thời điểm sử dụng
                record.getUsedParts().add(usageRecord);

                // Cộng dồn chi phí phụ tùng
                totalCost = totalCost.add(usageRecord.getPriceAtTimeOfUse().multiply(BigDecimal.valueOf(partDto.quantity())));

                // Kiểm tra và gửi cảnh báo nếu tồn kho chạm ngưỡng
                if (inventoryItem.getStockQuantity() <= inventoryItem.getMinStockLevel()) {
                    sendLowStockAlert(inventoryItem);
                }
            }
        }
        return totalCost;
    }

    /**
     * Phương thức private để tính toán các chi phí khác như tiền công, tiền được gói bảo hiểm chi trả, và chi phí phát sinh cuối cùng.
     */
    private void calculateAdditionalCosts(CreateServiceRecordRequest request, ServiceRecord record) {
        // Lấy tiền công từ request, mặc định là 0 nếu không có
        record.setLaborCost(request.getLaborCost() != null ? request.getLaborCost() : BigDecimal.ZERO);
        // Lấy số tiền được gói bảo dưỡng chi trả, mặc định là 0
        record.setCoveredByPackage(request.getCoveredByPackage() != null ? request.getCoveredByPackage() : BigDecimal.ZERO);

        // Tổng chi phí thực tế = Tổng tiền phụ tùng + Tiền công
        BigDecimal totalActualCost = record.getTotalPartsCost().add(record.getLaborCost());
        record.setTotalActualCost(totalActualCost);

        // Chi phí phát sinh = Tổng chi phí - Tiền gói chi trả
        BigDecimal additionalCost = totalActualCost.subtract(record.getCoveredByPackage());
        // Đảm bảo chi phí phát sinh không bao giờ là số âm
        record.setAdditionalCost(additionalCost.compareTo(BigDecimal.ZERO) > 0 ? additionalCost : BigDecimal.ZERO);
    }

    /**
     * Phương thức private để gửi email cảnh báo tồn kho thấp.
     */
    private void sendLowStockAlert(Inventory inventoryItem) {
        // Lấy danh sách người nhận là ADMIN và STAFF
        List<AppUser> recipients = userService.getUserListByRole("ADMIN");
        recipients.addAll(userService.getUserListByRole("STAFF"));

        // Lặp qua và gửi email cho từng người
        // EmailService nên được thiết kế để chạy bất đồng bộ (@Async)
        for (AppUser recipient : recipients) {
            emailService.sendLowStockAlertEmail(recipient, inventoryItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate) {
        return recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi từ Entity ServiceRecord sang DTO để trả về cho client.
     */
    private ServiceRecordResponseDto convertToDto(ServiceRecord record) {
        ServiceRecordResponseDto dto = new ServiceRecordResponseDto();
        dto.setId(record.getId());
        dto.setLicensePlate(record.getLicensePlate());
        dto.setKilometerReading(record.getKilometerReading());
        dto.setServiceDate(record.getServiceDate());
        dto.setCreatedBy(record.getCreatedBy());

        // Chuyển đổi danh sách chi tiết công việc
        if (record.getDetails() != null) {
            dto.setDetails(record.getDetails().stream().map(detail -> {
                ServiceDetailDto detailDto = new ServiceDetailDto();
                detailDto.setItemName(detail.getItemName());
                detailDto.setAction(detail.getAction());
                detailDto.setNotes(detail.getNotes());
                return detailDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setDetails(Collections.emptyList());
        }

        // Gán các thông tin tài chính vào DTO trả về
        dto.setTotalPartsCost(record.getTotalPartsCost());
        dto.setLaborCost(record.getLaborCost());
        dto.setTotalActualCost(record.getTotalActualCost());
        dto.setCoveredByPackage(record.getCoveredByPackage());
        dto.setAdditionalCost(record.getAdditionalCost());

        return dto;
    }
}