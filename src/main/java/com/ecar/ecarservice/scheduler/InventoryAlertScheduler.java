package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Center;
import com.ecar.ecarservice.entities.Inventory;
import com.ecar.ecarservice.repositories.CenterRepository;
import com.ecar.ecarservice.repositories.InventoryRepository;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Scheduler này chịu trách nhiệm tự động quét và gửi các cảnh báo liên quan đến tồn kho.
 */
@Component
@RequiredArgsConstructor
public class InventoryAlertScheduler {

    private static final Logger logger = LoggerFactory.getLogger(InventoryAlertScheduler.class);
    private final InventoryRepository inventoryRepository;
    private final CenterRepository centerRepository;
    private final UserService userService;
    private final EmailService emailService;

    // =================== GỬI BÁO CÁO TỒN KHO THẤP HÀNG NGÀY ===================
    /**
     * Tự động chạy vào 8 giờ sáng mỗi ngày.
     * Nhiệm vụ: Quét tất cả các trung tâm, tìm những phụ tùng đang dưới mức tồn kho tối thiểu
     * và gửi một email báo cáo tổng hợp cho tất cả Admin và Staff.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void sendDailyLowStockReport() {
        logger.info("Running daily low stock report job...");

        // Lấy danh sách người nhận là tất cả Admin và Staff
        List<AppUser> recipients = userService.getUserListByRole("ADMIN");
        recipients.addAll(userService.getUserListByRole("STAFF"));

        if (recipients.isEmpty()) {
            logger.warn("No recipients found for low stock report. Skipping job.");
            return;
        }

        // Lặp qua từng trung tâm để kiểm tra tồn kho riêng biệt
        List<Center> allCenters = centerRepository.findAll();
        for (Center center : allCenters) {
            // Tìm tất cả phụ tùng sắp hết hàng tại center này
            List<Inventory> lowStockItems = inventoryRepository.findLowStockPartsByCenter(center.getId());

            // Chỉ gửi email nếu có ít nhất một món sắp hết hàng
            if (!lowStockItems.isEmpty()) {
                logger.info("Found {} low stock items at center '{}'. Sending notifications...", lowStockItems.size(), center.getCenterName());
                // Gửi email báo cáo cho từng người trong danh sách nhận
                for (AppUser recipient : recipients) {
                    emailService.sendLowStockReportEmail(recipient, center, lowStockItems);
                }
            }
        }

        logger.info("Daily low stock report job finished.");
    }
}