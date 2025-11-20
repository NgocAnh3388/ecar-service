package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.repositories.VehicleRepository;
import com.ecar.ecarservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler này chịu trách nhiệm tự động gửi email nhắc nhở khách hàng
 * khi xe của họ sắp đến kỳ bảo dưỡng định kỳ tiếp theo.
 */
@Component
@RequiredArgsConstructor // Sử dụng Lombok để tự động tạo constructor
public class MaintenanceReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceReminderScheduler.class);
    private final VehicleRepository vehicleRepository;
    private final EmailService emailService;

    // Hẹn giờ nhắc nhở trước 10 ngày
    private static final int REMINDER_DAYS_BEFORE = 10;

    // =================== GỬI EMAIL NHẮC BẢO DƯỠNG ===================
    /**
     * Tự động chạy vào 8 giờ sáng mỗi ngày.
     * Nhiệm vụ: Tìm tất cả các xe có ngày bảo dưỡng tiếp theo là 10 ngày tới
     * và gửi email nhắc nhở cho chủ xe.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Chạy vào 8h sáng hàng ngày
    @Transactional(readOnly = true)
    public void sendUpcomingMaintenanceReminders() {
        logger.info("Running maintenance reminder job...");

        // Xác định ngày mục tiêu để gửi nhắc nhở (hôm nay + 10 ngày)
        LocalDate reminderTargetDate = LocalDate.now().plusDays(REMINDER_DAYS_BEFORE);

        // --- TỐI ƯU HÓA: Đẩy logic lọc xuống database ---
        // Thay vì `findAll()` rồi lọc trong Java, chúng ta tạo một query chuyên dụng.
        // Điều này hiệu quả hơn rất nhiều khi có hàng ngàn, hàng triệu bản ghi xe.
        List<Vehicle> upcomingVehicles = vehicleRepository.findVehiclesDueForMaintenanceOn(reminderTargetDate);

        if (upcomingVehicles.isEmpty()) {
            logger.info("No vehicles require maintenance reminders for due date: {}", reminderTargetDate);
            return;
        }

        logger.info("Found {} vehicles due for maintenance on {}. Sending reminders...", upcomingVehicles.size(), reminderTargetDate);

        // Lặp qua danh sách xe sắp đến hạn và gửi email
        for (Vehicle vehicle : upcomingVehicles) {
            // Lấy thông tin chủ xe
            AppUser owner = vehicle.getOwner();
            if (owner != null && owner.getEmail() != null) {
                try {
                    // Gọi EmailService để gửi mail (nên là @Async)
                    emailService.sendMaintenanceReminderEmail(owner, vehicle, vehicle.getNextDate().toLocalDate());
                    logger.info("Sent maintenance reminder to {} for vehicle {}", owner.getEmail(), vehicle.getLicensePlate());
                } catch (Exception e) {
                    logger.error("Failed to send reminder to {} for vehicle {}: {}", owner.getEmail(), vehicle.getLicensePlate(), e.getMessage());
                }
            } else {
                logger.warn("Vehicle with license plate {} has no owner or owner has no email.", vehicle.getLicensePlate());
            }
        }

        logger.info("Maintenance reminder job completed.");
    }
}