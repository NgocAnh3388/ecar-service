package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, AppUser currentUser);
    List<BookingResponseDto> getBookingsForCurrentUser(AppUser currentUser);

    List<BookingResponseDto> getAllBookings();
    BookingResponseDto cancelBookingByAdmin(Long bookingId);

    BookingResponseDto cancelBookingByCustomer(Long bookingId, AppUser currentUser);

}
