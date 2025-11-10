package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.repositories.VehicleRepository;
import com.ecar.ecarservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class MaintenanceReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceReminderScheduler.class);

    private final VehicleRepository vehicleRepository;
    private final EmailService emailService;

    // Nhac truoc 10 ngay
    private static final int REMINDER_DAYS_BEFORE = 10;

    public MaintenanceReminderScheduler(VehicleRepository vehicleRepository, EmailService emailService) {
        this.vehicleRepository = vehicleRepository;
        this.emailService = emailService;
    }

    /**
     * Chạy vào 8h sáng hàng ngày để nhắc bảo dưỡng cho các xe sắp tới hạn.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendUpcomingMaintenanceReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(REMINDER_DAYS_BEFORE);

        List<Vehicle> upcomingVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getNextDate() != null && v.getNextDate().toLocalDate().equals(reminderDate))
                .toList();

        if (upcomingVehicles.isEmpty()) {
            logger.info("No vehicles require maintenance reminders for date: {}", reminderDate);
            return;
        }

        for (Vehicle vehicle : upcomingVehicles) {
            AppUser owner = vehicle.getOwner();
            if (owner != null) {
                try {
                    emailService.sendMaintenanceReminderEmail(owner, vehicle, vehicle.getNextDate().toLocalDate());
                    logger.info("Sent maintenance reminder to {} for vehicle {}", owner.getEmail(), vehicle.getLicensePlate());
                } catch (Exception e) {
                    logger.error("Failed to send reminder to {}: {}", owner.getEmail(), e.getMessage());
                }
            }
        }

        logger.info("Maintenance reminder job completed for vehicles due on: {}", reminderDate);
    }
}
