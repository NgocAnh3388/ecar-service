package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.dto.BookingStatusDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.repositories.BookingRepository;
import com.ecar.ecarservice.service.BookingService;
import com.ecar.ecarservice.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final AppUserRepository appUserRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, EmailService emailService, AppUserRepository appUserRepository) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
        this.appUserRepository = appUserRepository;
    }

    // ====================================================================
    // == CUSTOMER-FACING METHODS  ==
    // ====================================================================
    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, AppUser currentUser) {
        Booking booking = new Booking();

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
        emailService.sendBookingConfirmationEmail(savedBooking);

        return convertToDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsForCurrentUser(AppUser currentUser) {
        return bookingRepository.findByUserId(currentUser.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBookingByCustomer(Long bookingId, AppUser currentUser) {
        Booking booking = findBookingById(bookingId);

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to cancel this booking.");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled by the customer.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        return convertToDto(cancelledBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingStatusDto getBookingStatus(Long bookingId, AppUser currentUser) {
        Booking booking = findBookingById(bookingId);
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this booking's status.");
        }
        return new BookingStatusDto(booking.getStatus());
    }

    // ====================================================================
    // == ADMIN/STAFF/TECHNICIAN METHODS ==
    // ====================================================================
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings(BookingStatus status) {
        List<Booking> bookings = (status != null) ? bookingRepository.findAllByStatus(status) : bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto confirmBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        validateStatusTransition(booking.getStatus(), BookingStatus.CONFIRMED);

        booking.setStatus(BookingStatus.CONFIRMED);
        emailService.sendBookingStatusUpdateEmail(booking, "Your Appointment is Confirmed!", "booking-confirmed-email");
        return convertToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto assignTechnician(Long bookingId, Long technicianId) {
        Booking booking = findBookingById(bookingId);
        AppUser technician = findUserById(technicianId);

        if (!technician.getRoles().contains(AppRole.TECHNICIAN)) {
            throw new IllegalArgumentException("User with ID " + technicianId + " is not a TECHNICIAN.");
        }
        validateStatusTransition(booking.getStatus(), BookingStatus.IN_PROGRESS);

        booking.setTechnician(technician);
        booking.setStatus(BookingStatus.IN_PROGRESS);

        emailService.sendNewTaskNotificationEmail(technician, booking);
        emailService.sendBookingStatusUpdateEmail(booking, "Your Car is Now In Service", "booking-status-update-email");
        return convertToDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getTasksForTechnician(Long technicianId) {
        List<BookingStatus> activeStatuses = List.of(BookingStatus.IN_PROGRESS, BookingStatus.CONFIRMED);
        return bookingRepository.findByTechnicianIdAndStatusIn(technicianId, activeStatuses)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = findBookingById(bookingId);
        validateStatusTransition(booking.getStatus(), newStatus);

        booking.setStatus(newStatus);
        String subject = "Your Appointment Status has been Updated to " + newStatus.name();
        emailService.sendBookingStatusUpdateEmail(booking, subject, "booking-status-update-email");
        return convertToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBookingByAdmin(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        validateStatusTransition(booking.getStatus(), BookingStatus.CANCELLED);

        booking.setStatus(BookingStatus.CANCELLED);
        return convertToDto(booking);
    }

    // ====================================================================
    // == PRIVATE HELPER METHODS ==
    // ====================================================================
    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));
    }

    private AppUser findUserById(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private void validateStatusTransition(BookingStatus from, BookingStatus to) {
        // Allow operations that do not change state (e.g. update notes)
        if (from == to) {
            return;
        }

        EnumSet<BookingStatus> allowedTransitions;

        switch (from) {
            case PENDING:
                // From PENDING: CONFIRMED (Staff confirmed) or CANCELLED (Customer/Staff canceled)
                allowedTransitions = EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.CANCELLED);
                break;
            case CONFIRMED:
                // From CONFIRMED: CHECKED_IN (Customer arrival), IN_PROGRESS (assign Technician), or CANCELLED (Staff cancellation)
                allowedTransitions = EnumSet.of(BookingStatus.CHECKED_IN, BookingStatus.IN_PROGRESS, BookingStatus.CANCELLED);
                break;
            case CHECKED_IN:
                // From CHECKED_IN: IN_PROGRESS (assign Technician)
                allowedTransitions = EnumSet.of(BookingStatus.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                // From IN_PROGRESS: COMPLETED (Technician finished)
                allowedTransitions = EnumSet.of(BookingStatus.COMPLETED);
                break;
            case COMPLETED:
                // From COMPLETED: CLOSED (Customer paid)
                allowedTransitions = EnumSet.of(BookingStatus.CLOSED);
                break;
            case CANCELLED:
            case CLOSED:
                // CANCELLED and CLOSED are final, immutable states.
                allowedTransitions = EnumSet.noneOf(BookingStatus.class);
                break;
            default:
                // Deny unknown/unsupported current statuses
                allowedTransitions = EnumSet.noneOf(BookingStatus.class);
                break;
        }

        if (!allowedTransitions.contains(to)) {
            throw new IllegalStateException("Cannot transition booking status from " + from + " to " + to);
        }
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

        // user information
        if (booking.getUser() != null) {
            BookingResponseDto.UserInBookingDto userDto = new BookingResponseDto.UserInBookingDto();
            userDto.setId(booking.getUser().getId());
            userDto.setEmail(booking.getUser().getEmail());
            userDto.setFullName(booking.getUser().getFullName());
            dto.setUser(userDto);
        }
        // technician information
        if (booking.getTechnician() != null) {
            BookingResponseDto.UserInBookingDto techDto = new BookingResponseDto.UserInBookingDto();
            techDto.setId(booking.getTechnician().getId());
            techDto.setEmail(booking.getTechnician().getEmail());
            techDto.setFullName(booking.getTechnician().getFullName());
            dto.setTechnician(techDto);
        }

        return dto;
    }
}