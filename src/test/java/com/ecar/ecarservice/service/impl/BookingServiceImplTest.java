//package com.ecar.ecarservice.service.impl;
//
//import com.ecar.ecarservice.dto.BookingRequestDto;
//import com.ecar.ecarservice.dto.BookingResponseDto;
//import com.ecar.ecarservice.entities.AppUser;
//import com.ecar.ecarservice.entities.Booking;
//import com.ecar.ecarservice.enums.BookingStatus;
//import com.ecar.ecarservice.repositories.BookingRepository;
//import com.ecar.ecarservice.service.EmailService;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.access.AccessDeniedException;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BookingServiceImplTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private EmailService emailService;
//
//    @InjectMocks
//    private BookingServiceImpl bookingService;
//
//    private AppUser testUser;
//    private BookingRequestDto bookingRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        // Chuẩn bị dữ liệu mẫu dùng chung cho các test
//        testUser = new AppUser();
//        testUser.setId(1L);
//        testUser.setEmail("test@example.com");
//
//        bookingRequestDto = new BookingRequestDto();
//        bookingRequestDto.setLicensePlate("51K-12345");
//        bookingRequestDto.setCarModel("Toyota Vios");
//        // Mặc định là một thời gian hợp lệ trong tương lai
//        bookingRequestDto.setAppointmentDateTime(LocalDateTime.now().plusDays(5));
//    }
//
//    // =============================================
//    //         HAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("createBooking_Success: Should save booking and send confirmation email")
//    void testCreateBooking_Success() {
//        // Arrange
//        Booking savedBooking = new Booking(); // Giả lập đối tượng booking sau khi được lưu
//        savedBooking.setId(100L);
//        savedBooking.setUser(testUser);
//        savedBooking.setLicensePlate(bookingRequestDto.getLicensePlate());
//        savedBooking.setStatus(BookingStatus.PENDING);
//
//        // Giả lập hành vi của repository
//        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
//        // Giả lập hành vi của email service (không làm gì cả)
//        doNothing().when(emailService).sendBookingConfirmationEmail(any(Booking.class));
//
//        // Act
//        BookingResponseDto result = bookingService.createBooking(bookingRequestDto, testUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//        assertEquals("51K-12345", result.getLicensePlate());
//        assertEquals(BookingStatus.PENDING, result.getStatus());
//
//        // Kiểm tra xem các hàm có được gọi không
//        verify(bookingRepository, times(1)).save(any(Booking.class));
//        verify(emailService, times(1)).sendBookingConfirmationEmail(any(Booking.class));
//    }
//
//    @Test
//    @DisplayName("cancelBookingByCustomer_Success: Should cancel a PENDING booking")
//    void testCancelBookingByCustomer_Success() {
//        // Arrange
//        Long bookingId = 1L;
//        Booking existingBooking = new Booking();
//        existingBooking.setId(bookingId);
//        existingBooking.setUser(testUser);
//        existingBooking.setStatus(BookingStatus.PENDING);
//
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        BookingResponseDto result = bookingService.cancelBookingByCustomer(bookingId, testUser);
//
//        // Assert
//        assertEquals(BookingStatus.CANCELLED, result.getStatus());
//        verify(bookingRepository, times(1)).findById(bookingId);
//        verify(bookingRepository, times(1)).save(existingBooking);
//    }
//
//    @Test
//    @DisplayName("getBookingsForCurrentUser: Should return list of bookings for the user")
//    void testGetBookingsForCurrentUser() {
//        // Arrange
//        Booking booking1 = new Booking();
//        booking1.setId(101L);
//        booking1.setUser(testUser);
//
//        when(bookingRepository.findByUserId(testUser.getId())).thenReturn(List.of(booking1));
//
//        // Act
//        List<BookingResponseDto> result = bookingService.getBookingsForCurrentUser(testUser);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(101L, result.get(0).getId());
//        verify(bookingRepository, times(1)).findByUserId(testUser.getId());
//    }
//
//    @Test
//    @DisplayName("getAllBookings: Should return all bookings")
//    void testGetAllBookings() {
//        // Arrange
//        Booking booking1 = new Booking();
//        booking1.setId(101L);
//        booking1.setUser(testUser);
//
//        Booking booking2 = new Booking();
//        booking2.setId(102L);
//        booking2.setUser(null); // Test trường hợp user là null
//
//        when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2));
//
//        // Act
//        List<BookingResponseDto> result = bookingService.getAllBookings();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertNotNull(result.get(0).getUser());
//        assertNull(result.get(1).getUser()); // Khẳng định trường hợp user null được xử lý đúng
//        verify(bookingRepository, times(1)).findAll();
//    }
//
//    @Test
//    @DisplayName("cancelBookingByAdmin_Success: Should cancel any booking")
//    void testCancelBookingByAdmin_Success() {
//        // Arrange
//        Long bookingId = 50L;
//        Booking existingBooking = new Booking();
//        existingBooking.setId(bookingId);
//        existingBooking.setStatus(BookingStatus.PENDING);
//
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        BookingResponseDto result = bookingService.cancelBookingByAdmin(bookingId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(BookingStatus.CANCELLED, result.getStatus());
//        verify(bookingRepository, times(1)).save(any(Booking.class));
//    }
//
//    // =============================================
//    //         UNHAPPYCASE
//    // =============================================
//
//    @Test
//    @DisplayName("createBooking_InvalidTime: Should throw exception for past appointment time")
//    void testCreateBooking_InvalidTime_ThrowsException() {
//        // Arrange
//        // Set thời gian trong quá khứ
//        bookingRequestDto.setAppointmentDateTime(LocalDateTime.now().minusDays(1));
//
//        // Act & Assert
//        // Dựa trên code mới, nó sẽ ném IllegalStateException
//        Exception exception = assertThrows(IllegalStateException.class, () -> {
//            bookingService.createBooking(bookingRequestDto, testUser);
//        });
//
//        // Kiểm tra message lỗi
//        assertEquals("Appointment date and time cannot be in the past.", exception.getMessage());
//
//        // Đảm bảo không có gì được lưu vào DB hoặc gửi mail
//        verify(bookingRepository, never()).save(any());
//        verify(emailService, never()).sendBookingConfirmationEmail(any());
//    }
//
//    @Test
//    @DisplayName("cancelBookingByCustomer_NotFound: Should throw EntityNotFoundException")
//    void testCancelBookingByCustomer_NotFound() {
//        // Arrange
//        Long bookingId = 99L;
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            bookingService.cancelBookingByCustomer(bookingId, testUser);
//        });
//
//        verify(bookingRepository, never()).save(any());
//    }
//
//
//    @Test
//    @DisplayName("cancelBookingByCustomer_AccessDenied: Should throw exception when user is not the owner")
//    void testCancelBookingByCustomer_AccessDenied() {
//        // Arrange
//        Long bookingId = 1L;
//        AppUser anotherUser = new AppUser();
//        anotherUser.setId(2L); // User khác
//
//        Booking existingBooking = new Booking();
//        existingBooking.setId(bookingId);
//        existingBooking.setUser(anotherUser); // Booking này thuộc về anotherUser
//        existingBooking.setStatus(BookingStatus.PENDING);
//
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
//
//        // Act & Assert
//        assertThrows(AccessDeniedException.class, () -> {
//            // testUser (ID=1) cố gắng hủy booking của anotherUser (ID=2)
//            bookingService.cancelBookingByCustomer(bookingId, testUser);
//        });
//
//        verify(bookingRepository, never()).save(any()); // Hàm save không bao giờ được gọi
//    }
//
//    @Test
//    @DisplayName("cancelBookingByCustomer_IllegalState: Should throw exception for non-PENDING bookings")
//    void testCancelBookingByCustomer_IllegalState() {
//        // Arrange
//        Long bookingId = 1L;
//        Booking existingBooking = new Booking();
//        existingBooking.setId(bookingId);
//        existingBooking.setUser(testUser);
//        existingBooking.setStatus(BookingStatus.COMPLETED); // Trạng thái không phải PENDING
//
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalStateException.class, () -> {
//            bookingService.cancelBookingByCustomer(bookingId, testUser);
//        });
//
//        assertTrue(exception.getMessage().contains("cannot be cancelled"));
//        verify(bookingRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("cancelBookingByAdmin_NotFound: Should throw EntityNotFoundException")
//    void testCancelBookingByAdmin_NotFound() {
//        // Arrange
//        Long bookingId = 99L;
//        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            bookingService.cancelBookingByAdmin(bookingId);
//        });
//    }
//
//    @Test
//    @DisplayName("createBooking_Fails_WhenEmailServiceThrowsException: Should not save booking")
//    void testCreateBooking_Fails_WhenEmailServiceThrowsException() {
//        // Arrange
//        // Giả lập EmailService ném ra một ngoại lệ khi được gọi
//        doThrow(new RuntimeException("Email server is down")).when(emailService).sendBookingConfirmationEmail(any(Booking.class));
//
//        // Giả lập hàm save của repository vẫn hoạt động bình thường
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act & Assert
//        // Vì hàm createBooking được đánh dấu @Transactional, khi emailService ném lỗi,
//        // toàn bộ giao dịch sẽ được rollback. Do đó, chúng ta mong đợi một RuntimeException.
//        assertThrows(RuntimeException.class, () -> {
//            bookingService.createBooking(bookingRequestDto, testUser);
//        });
//
//        // Kiểm tra xem emailService có được gọi không (dù nó ném lỗi)
//        verify(emailService, times(1)).sendBookingConfirmationEmail(any(Booking.class));
//        // Việc kiểm tra rollback (hàm save không có hiệu lực) cần test tích hợp,
//        // ở unit test chúng ta chỉ có thể xác nhận exception được ném ra.
//    }
//
//    @Test
//    @DisplayName("getBookingsForCurrentUser_ReturnsEmptyList_WhenNoBookingsFound")
//    void testGetBookingsForCurrentUser_ReturnsEmptyList() {
//        // Arrange
//        // Giả lập repository trả về một danh sách rỗng
//        when(bookingRepository.findByUserId(testUser.getId())).thenReturn(List.of());
//
//        // Act
//        List<BookingResponseDto> result = bookingService.getBookingsForCurrentUser(testUser);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty()); // Mong đợi danh sách trả về là rỗng
//        verify(bookingRepository, times(1)).findByUserId(testUser.getId());
//    }
//
//    @Test
//    @DisplayName("getAllBookings_ReturnsEmptyList_WhenNoBookingsExist")
//    void testGetAllBookings_ReturnsEmptyList() {
//        // Arrange
//        when(bookingRepository.findAll()).thenReturn(List.of());
//
//        // Act
//        List<BookingResponseDto> result = bookingService.getAllBookings();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(bookingRepository, times(1)).findAll();
//    }
//}