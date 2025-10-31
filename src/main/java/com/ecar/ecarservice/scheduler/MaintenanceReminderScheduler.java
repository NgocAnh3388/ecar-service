package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.enitiies.AppUser;
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
    private final MaintenanceService maintenanceService; // <-- INJECT SERVICE MỚI

    // Bảo dưỡng định kỳ là 12 tháng
    private static final int MAINTENANCE_INTERVAL_MONTHS = 12;
    // Nhắc nhở trước 15 ngày
    private static final int REMINDER_DAYS_BEFORE = 15;

    public MaintenanceReminderScheduler(ServiceRecordRepository serviceRecordRepository, EmailService emailService, MaintenanceService maintenanceService) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.emailService = emailService;
        this.maintenanceService = maintenanceService;
    }


//    @Scheduled(fixedRate = 30000) // Chạy mỗi 30 giây
//    @Scheduled(cron = "0 0 8 * * ?") // Chạy vào 8h sáng hàng ngày
//    @Transactional(readOnly = true)
//    public void checkAndSendDateBasedReminders() {
//        System.out.println("Running date-based maintenance reminder job...");
//
//        // Xác định ngày mục tiêu để gửi nhắc nhở
//        LocalDate today = LocalDate.now();
//        LocalDate reminderTargetDate = today.plusDays(REMINDER_DAYS_BEFORE);
//
//        // Lấy tất cả các bản ghi lịch sử dịch vụ
//        List<ServiceRecord> allRecords = serviceRecordRepository.findAll();
//
//        allRecords.forEach(lastRecord -> {
//            LocalDate lastServiceDate = lastRecord.getServiceDate().toLocalDate();
//
//            // Tính ngày bảo dưỡng tiếp theo dự kiến
//            LocalDate nextDueDate = lastServiceDate.plusMonths(MAINTENANCE_INTERVAL_MONTHS);
//
//            // Kiểm tra ngày đáo hạn
//            if (nextDueDate.isEqual(reminderTargetDate)) {
//                System.out.println("Found a vehicle due for maintenance: License Plate " + lastRecord.getLicensePlate());
//
//                AppUser user = lastRecord.getBooking().getUser();
//                String licensePlate = lastRecord.getLicensePlate();
//
//                // Ước tính số km hiện tại. Giả sử xe đi trung bình 1000 km/tháng
//                int estimatedCurrentKm = lastRecord.getKilometerReading() + (MAINTENANCE_INTERVAL_MONTHS * 1000);
//
//                // Lấy danh sách hạng mục dưới dạng List<MaintenanceScheduleDto>
//                List<MaintenanceScheduleDto> scheduleItems = maintenanceService.getScheduleByKilometers(estimatedCurrentKm);
//
//                // Truyền cả đối tượng `user` và `scheduleItems` vào hàm gửi email
//                emailService.sendDateBasedMaintenanceReminderEmail(user, licensePlate, nextDueDate, scheduleItems);
//
//            }
//        });
//        System.out.println("Date-based reminder job finished.");
//    }
}
