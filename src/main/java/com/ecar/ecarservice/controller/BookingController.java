package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.BookingRequestDto;
import com.ecar.ecarservice.dto.BookingResponseDto;
import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.service.BookingService;
import com.ecar.ecarservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * các API mà KHÁCH HÀNG (Customer) sử dụng để quản lý lịch hẹn (Bookings) của chính họ.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor // Tự động tạo constructor cho các trường `final`
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    // =================== TẠO LỊCH HẸN MỚI ===================
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingDto,
            @AuthenticationPrincipal OidcUser oidcUser) {

        // Lấy thông tin người dùng từ DB dựa trên thông tin đăng nhập
        AppUser currentUser = userService.getCurrentUser(oidcUser);

        // Gọi đến service để xử lý logic tạo booking
        BookingResponseDto newBookingDto = bookingService.createBooking(bookingDto, currentUser);

        // Trả về response 201 Created cùng với dữ liệu của booking mới
        return new ResponseEntity<>(newBookingDto, HttpStatus.CREATED);
    }

    // =================== XEM CÁC LỊCH HẸN CỦA TÔI ===================
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(@AuthenticationPrincipal OidcUser oidcUser) {
        // Tái sử dụng logic để lấy người dùng hiện tại
        AppUser currentUser = userService.getCurrentUser(oidcUser);

        List<BookingResponseDto> bookings = bookingService.getBookingsForCurrentUser(currentUser);
        return ResponseEntity.ok(bookings);
    }

    // =================== KHÁCH HÀNG TỰ HỦY LỊCH HẸN ===================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBookingByCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal OidcUser oidcUser) {

        try {
            // 1. Lấy thông tin User hiện tại từ Token
            AppUser currentUser = userService.getCurrentUser(oidcUser);

            // 2. Gọi Service xử lý
            BookingResponseDto cancelledBooking = bookingService.cancelBookingByCustomer(id, currentUser);

            return ResponseEntity.ok(cancelledBooking);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Lỗi do trạng thái không phải PENDING
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}