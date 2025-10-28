package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.dto.BookingStatusDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, AppUser currentUser);
    List<BookingResponseDto> getBookingsForCurrentUser(AppUser currentUser);

    List<BookingResponseDto> getAllBookings(BookingStatus status);
    BookingResponseDto cancelBookingByAdmin(Long bookingId);

    BookingResponseDto cancelBookingByCustomer(Long bookingId, AppUser currentUser);

    BookingResponseDto confirmBooking(Long bookingId);
    BookingResponseDto updateBookingStatus(Long bookingId, BookingStatus newStatus);
    BookingStatusDto getBookingStatus(Long bookingId, AppUser currentUser);
    BookingResponseDto assignTechnician(Long bookingId, Long technicianId);
    List<BookingResponseDto> getTasksForTechnician(Long technicianId);

}