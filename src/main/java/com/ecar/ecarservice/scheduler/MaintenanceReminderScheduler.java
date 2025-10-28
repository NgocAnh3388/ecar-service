package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.enitiies.ServiceRecord;
import com.ecar.ecarservice.repositories.ServiceRecordRepository;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.MaintenanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class MaintenanceReminderScheduler {

    private final ServiceRecordRepository serviceRecordRepository;
    private final EmailService emailService;
    private final MaintenanceService maintenanceService;

    private static final int MAINTENANCE_INTERVAL_MONTHS = 12;
    private static final int REMINDER_DAYS_BEFORE = 15;

    public MaintenanceReminderScheduler(ServiceRecordRepository serviceRecordRepository, EmailService emailService, MaintenanceService maintenanceService) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.emailService = emailService;
        this.maintenanceService = maintenanceService;
    }


    //    @Scheduled(fixedRate = 30000)
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void checkAndSendDateBasedReminders() {
        System.out.println("Running date-based maintenance reminder job...");

        LocalDate today = LocalDate.now();
        LocalDate reminderTargetDate = today.plusDays(REMINDER_DAYS_BEFORE);

        List<ServiceRecord> allRecords = serviceRecordRepository.findAll();
        allRecords.forEach(lastRecord -> {
            LocalDate lastServiceDate = lastRecord.getServiceDate().toLocalDate();

            LocalDate nextDueDate = lastServiceDate.plusMonths(MAINTENANCE_INTERVAL_MONTHS);
            if (nextDueDate.isEqual(reminderTargetDate)) {
                Booking booking = lastRecord.getBooking();
                if (booking == null) {
                    return;
                }
                System.out.println("Found a vehicle due for maintenance: License Plate " + lastRecord.getLicensePlate());

                AppUser user = booking.getUser();
                String licensePlate = lastRecord.getLicensePlate();
                String carModel = booking.getCarModel();

                int estimatedCurrentKm = lastRecord.getKilometerReading() + (MAINTENANCE_INTERVAL_MONTHS * 1000);
                List<MaintenanceScheduleDto> scheduleItems = maintenanceService.getScheduleByKilometers(carModel, estimatedCurrentKm);

                emailService.sendDateBasedMaintenanceReminderEmail(user, licensePlate, nextDueDate, scheduleItems);
            }
        });
        System.out.println("Date-based reminder job finished.");
    }
}