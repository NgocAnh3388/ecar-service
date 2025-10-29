package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class BookingAdminController {

    private final BookingService bookingService;

    public BookingAdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // API 1: Lấy tất cả booking
    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // API 2: Xóa một booking theo ID
    @PutMapping("/{id}/cancel") // Dùng PUT và đường dẫn rõ nghĩa hơn
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long id) {
        BookingResponseDto cancelledBooking = bookingService.cancelBookingByAdmin(id);
        return ResponseEntity.ok(cancelledBooking);
    }
}
