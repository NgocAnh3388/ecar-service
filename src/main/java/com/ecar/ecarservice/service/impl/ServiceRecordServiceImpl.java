//package com.ecar.ecarservice.service.impl;
//
//import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
//import com.ecar.ecarservice.dto.ServiceDetailDto;
//import com.ecar.ecarservice.dto.ServiceRecordResponseDto;
//import com.ecar.ecarservice.enitiies.*;
//import com.ecar.ecarservice.enums.BookingStatus;
//import com.ecar.ecarservice.enums.MaintenanceAction;
//import com.ecar.ecarservice.repositories.BookingRepository;
//import com.ecar.ecarservice.repositories.CarModelRepository;
//import com.ecar.ecarservice.repositories.ServiceRecordRepository;
//import com.ecar.ecarservice.service.ServiceRecordService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ServiceRecordServiceImpl implements ServiceRecordService {
//
//    private final ServiceRecordRepository recordRepository;
//    private final BookingRepository bookingRepository;
//    private final MaintenanceScheduleRepository scheduleRepository;
//    private final CarModelRepository carModelRepository;
//
//    public ServiceRecordServiceImpl(
//            ServiceRecordRepository recordRepository,
//            BookingRepository bookingRepository,
//            MaintenanceScheduleRepository scheduleRepository,
//            CarModelRepository carModelRepository) {
//        this.recordRepository = recordRepository;
//        this.bookingRepository = bookingRepository;
//        this.scheduleRepository = scheduleRepository;
//        this.carModelRepository = carModelRepository;
//    }
//
//    @Override
//    @Transactional
//    public ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request) {
//        Booking booking = bookingRepository.findById(request.getBookingId())
//                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + request.getBookingId()));
//
//        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CLOSED) {
//            throw new IllegalStateException("This booking has already been processed.");
//        }
//
//        ServiceRecord record = new ServiceRecord();
//        record.setBooking(booking);
//        record.setLicensePlate(booking.getLicensePlate());
//        record.setKilometerReading(request.getKilometerReading());
//        record.setServiceDate(LocalDateTime.now());
//
//        List<ServiceRecordDetail> details = request.getDetails().stream().map(dto -> {
//            ServiceRecordDetail detail = new ServiceRecordDetail();
//            detail.setItemName(dto.getItemName());
//            detail.setAction(dto.getAction());
//            detail.setNotes(dto.getNotes());
//
//            BigDecimal price = findPriceForItem(booking.getCarModel(), dto.getItemName(), dto.getAction(), request.getKilometerReading());
//            detail.setPrice(price);
//
//            detail.setServiceRecord(record);
//            return detail;
//        }).collect(Collectors.toList());
//
//        record.setDetails(details);
//        booking.setStatus(BookingStatus.COMPLETED);
//
//        ServiceRecord savedRecord = recordRepository.save(record);
//        return convertToDto(savedRecord);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate) {
//        return recordRepository.findByLicensePlateOrderByServiceDateDesc(licensePlate)
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public ServiceRecordResponseDto addDetailToRecord(Long recordId, ServiceDetailDto detailDto) {
//        ServiceRecord record = recordRepository.findById(recordId)
//                .orElseThrow(() -> new EntityNotFoundException("Service Record not found with id: " + recordId));
//
//        ServiceRecordDetail newDetail = new ServiceRecordDetail();
//        newDetail.setItemName(detailDto.getItemName());
//        newDetail.setAction(detailDto.getAction());
//        newDetail.setNotes(detailDto.getNotes());
//
//        String carModel = record.getBooking().getCarModel();
//        int kilometers = record.getKilometerReading();
//        BigDecimal price = findPriceForItem(carModel, detailDto.getItemName(), detailDto.getAction(), kilometers);
//        newDetail.setPrice(price);
//
//        newDetail.setServiceRecord(record);
//        record.getDetails().add(newDetail);
//
//        return convertToDto(record);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ServiceRecordResponseDto getServiceRecordById(Long id) {
//        ServiceRecord record = recordRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Service Record not found with id: " + id));
//        return convertToDto(record);
//    }
//
//    private BigDecimal findPriceForItem(String carModelName, String itemName, MaintenanceAction action, int currentKilometers) {
//        if (carModelName == null || itemName == null || action == null) {
//            return BigDecimal.ZERO;
//        }
//
//        CarModel carModel = carModelRepository.findByNameIgnoreCase(carModelName)
//                .orElse(null);
//
//        if (carModel == null) {
//            return BigDecimal.ZERO;
//        }
//
//        Integer closestMark = scheduleRepository.findClosestKilometerMark(carModel.getId(), currentKilometers)
//                .orElse(null);
//
//        if (closestMark == null) {
//            return BigDecimal.ZERO;
//        }
//
//        return scheduleRepository.findScheduleByDetails(carModelName, itemName, action, closestMark)
//                .map(MaintenanceSchedule::getPrice)
//                .orElse(BigDecimal.ZERO);
//    }
//
//    private ServiceRecordResponseDto convertToDto(ServiceRecord record) {
//        ServiceRecordResponseDto recordDto = new ServiceRecordResponseDto();
//        recordDto.setId(record.getId());
//        recordDto.setLicensePlate(record.getLicensePlate());
//        recordDto.setKilometerReading(record.getKilometerReading());
//        recordDto.setServiceDate(record.getServiceDate());
//        recordDto.setCreatedBy(record.getCreatedBy());
//
//        List<ServiceDetailDto> detailDtos = record.getDetails().stream().map(detail -> {
//            ServiceDetailDto detailDto = new ServiceDetailDto();
//            detailDto.setItemName(detail.getItemName());
//            detailDto.setAction(detail.getAction());
//            detailDto.setNotes(detail.getNotes());
//            detailDto.setPrice(detail.getPrice());
//            return detailDto;
//        }).collect(Collectors.toList());
//
//        recordDto.setDetails(detailDtos);
//
//        BigDecimal totalPrice = detailDtos.stream()
//                .map(detail -> detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        recordDto.setTotalPrice(totalPrice);
//
//        return recordDto;
//    }
//}