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

/**
 * Class này hoạt động như một "lưới an toàn" tập trung,
 * bắt tất cả các Exception được ném ra từ các Controller
 * và chuyển đổi chúng thành các response lỗi JSON đẹp đẽ cho client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các lỗi "Không tìm thấy tài nguyên".
     * Ví dụ: repository.findById(...).orElseThrow(EntityNotFoundException::new)
     * @return 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Xử lý các lỗi logic nghiệp vụ hoặc tham số không hợp lệ.
     * Ví dụ: Đặt lịch trong quá khứ, hủy booking đã hoàn thành, tạo phụ tùng với carModelId không tồn tại.
     * @return 400 Bad Request
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBusinessLogicException(RuntimeException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý các lỗi validation từ annotation @Valid trên @RequestBody.
     * @return 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Failed",
                "message", errorMessage
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý các lỗi phân quyền (khi user đã đăng nhập nhưng không có vai trò phù hợp).
     * Được kích hoạt bởi @PreAuthorize.
     * @return 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Forbidden",
                "message", "You do not have permission to access this resource."
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * "Lưới an toàn cuối cùng": Bắt tất cả các lỗi không lường trước khác.
     * Luôn được đặt ở cuối cùng.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // In ra stack trace đầy đủ trong log của server để dev có thể debug
        ex.printStackTrace();

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please contact support." // Che giấu chi tiết lỗi với client
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}