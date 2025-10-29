package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.repositories.BookingRepository;
import com.ecar.ecarservice.service.BookingService;
import com.ecar.ecarservice.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService; // Inject EmailService
    }

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, AppUser currentUser) {
        Booking booking = new Booking();

        // Map thông tin từ DTO sang Entity
        booking.setUser(currentUser);
        booking.setCustomerPhoneNumber(bookingDto.getCustomerPhoneNumber());
        booking.setLicensePlate(bookingDto.getLicensePlate());
        booking.setCarModel(bookingDto.getCarModel());
        booking.setVinNumber(bookingDto.getVinNumber());
        booking.setServiceCenter(bookingDto.getServiceCenter());
        booking.setAppointmentDateTime(bookingDto.getAppointmentDateTime());
        booking.setServiceAdvisor(bookingDto.getServiceAdvisor());
        booking.setNotes(bookingDto.getNotes());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // --- GỌI GỬI MAIL ---
        emailService.sendBookingConfirmationEmail(savedBooking);

        // Chuyển đổi sang DTO trước khi trả về
        return convertToDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsForCurrentUser(AppUser currentUser) {
        List<Booking> bookings = bookingRepository.findByUserId(currentUser.getId());
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBookingByAdmin(Long bookingId) {
        // 1. Tìm booking trong database
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        // 2. Cập nhật trạng thái của nó
        booking.setStatus(BookingStatus.CANCELLED);

        // 3. Lưu lại vào database
        Booking cancelledBooking = bookingRepository.save(booking);

        // 4. Chuyển đổi sang DTO và trả về để xác nhận
        return convertToDto(cancelledBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBookingByCustomer(Long bookingId, AppUser currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to cancel this booking.");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("This booking cannot be cancelled as it has already been " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        return convertToDto(cancelledBooking);
    }

    private BookingResponseDto convertToDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setCustomerPhoneNumber(booking.getCustomerPhoneNumber());
        dto.setLicensePlate(booking.getLicensePlate());
        dto.setCarModel(booking.getCarModel());
        dto.setVinNumber(booking.getVinNumber());
        dto.setServiceCenter(booking.getServiceCenter());
        dto.setAppointmentDateTime(booking.getAppointmentDateTime());
        dto.setServiceAdvisor(booking.getServiceAdvisor());
        dto.setNotes(booking.getNotes());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setCreatedBy(booking.getCreatedBy());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setUpdatedBy(booking.getUpdatedBy());

        // Chuyển đổi thông tin user
        if (booking.getUser() != null) {
            BookingResponseDto.UserInBookingDto userDto = new BookingResponseDto.UserInBookingDto();
            userDto.setId(booking.getUser().getId());
            userDto.setEmail(booking.getUser().getEmail());
            dto.setUser(userDto);
        }

        return dto;
    }
}
