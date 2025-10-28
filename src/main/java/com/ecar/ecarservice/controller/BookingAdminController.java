package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.enums.BookingStatus;
import com.ecar.ecarservice.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class BookingAdminController {

    private final BookingService bookingService;

    public BookingAdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(
            @RequestParam(required = false) BookingStatus status) {
        return ResponseEntity.ok(bookingService.getAllBookings(status));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long id) {
        BookingResponseDto cancelledBooking = bookingService.cancelBookingByAdmin(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDto> confirmBooking(@PathVariable Long id) {
        BookingResponseDto confirmedBooking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(confirmedBooking);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> updateStatus(@PathVariable Long id, @RequestBody BookingStatus status) {
        BookingResponseDto updatedBooking = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(updatedBooking);
    }

    @PutMapping("/{bookingId}/assign")
    public ResponseEntity<BookingResponseDto> assignTechnician(
            @PathVariable Long bookingId,
            @RequestParam Long technicianId) {
        return ResponseEntity.ok(bookingService.assignTechnician(bookingId, technicianId));
    }
}