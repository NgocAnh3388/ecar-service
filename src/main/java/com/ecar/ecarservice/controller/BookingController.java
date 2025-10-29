package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AppUserRepository appUserRepository;

    public BookingController(BookingService bookingService, AppUserRepository appUserRepository) {
        this.bookingService = bookingService;
        this.appUserRepository = appUserRepository;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestBody BookingRequestDto bookingDto,
            @AuthenticationPrincipal OidcUser oidcUser) {

        AppUser currentUser = appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found in database"));

        BookingResponseDto newBookingDto = bookingService.createBooking(bookingDto, currentUser);
        return new ResponseEntity<>(newBookingDto, HttpStatus.CREATED);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(@AuthenticationPrincipal OidcUser oidcUser) {
        AppUser currentUser = appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found in database"));

        List<BookingResponseDto> bookings = bookingService.getBookingsForCurrentUser(currentUser);
        return ResponseEntity.ok(bookings);
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBookingByCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal OidcUser oidcUser) {

        AppUser currentUser = appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found in database"));

        BookingResponseDto cancelledBooking = bookingService.cancelBookingByCustomer(id, currentUser);
        return ResponseEntity.ok(cancelledBooking);
    }

}
