package com.ecar.ecarservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

// Annotation này sẽ quét tất cả các @RestController để bắt exception
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Hàm này sẽ được gọi khi có EntityNotFoundException bị ném ra
    // Ví dụ: repository.findById(...).orElseThrow(...)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        // Tạo một đối tượng JSON để trả về cho client
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(), // 404
                "error", "Not Found",
                "message", ex.getMessage() // Lấy message từ exception
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Hàm này sẽ được gọi khi có IllegalStateException hoặc IllegalArgumentException
    // Ví dụ: đặt lịch trong quá khứ, hủy booking đã hoàn thành
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(RuntimeException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(), // 400
                "error", "Bad Request",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Hàm này sẽ được gọi khi có AccessDeniedException (lỗi phân quyền)
    // Ví dụ: Customer cố gắng truy cập API của Admin
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.FORBIDDEN.value(), // 403
                "error", "Forbidden",
                "message", "You do not have permission to access this resource." // Message chung để bảo mật
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // (Tùy chọn) Bắt tất cả các lỗi khác và trả về 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Log lỗi này ra để debug
        ex.printStackTrace();

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please contact support."
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy tất cả các lỗi validation và gộp chúng thành một chuỗi
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(), // 400
                "error", "Validation Failed",
                "message", errorMessage
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        // Log lỗi này ra để xem nó là gì
        System.err.println("Caught a RuntimeException: " + ex.getMessage());

        // Kiểm tra xem có phải lỗi "Car model not found" không
        if (ex.getMessage() != null && ex.getMessage().contains("Car model not found")) {
            Map<String, Object> body = Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.NOT_FOUND.value(), // Trả về 404 thì hợp lý hơn
                    "error", "Not Found",
                    "message", ex.getMessage()
            );
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        // Nếu là một RuntimeException khác, trả về 500
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred."
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}