package com.ecar.ecarservice.scheduler;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Center;
import com.ecar.ecarservice.entities.Inventory;
import com.ecar.ecarservice.repositories.CenterRepository;
import com.ecar.ecarservice.repositories.InventoryRepository;
import com.ecar.ecarservice.service.EmailService;
import com.ecar.ecarservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryAlertScheduler {

    private final InventoryRepository inventoryRepository;
    private final CenterRepository centerRepository;
    private final UserService userService;
    private final EmailService emailService;

    /**
     * Chạy vào 8h sáng hàng ngày để gửi báo cáo tồn kho.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void sendDailyLowStockReport() {
        System.out.println("Running daily low stock report job...");

        List<AppUser> recipients = userService.getUserListByRole("ADMIN");
        recipients.addAll(userService.getUserListByRole("STAFF"));

        List<Center> allCenters = centerRepository.findAll();

        for (Center center : allCenters) {
            // Tìm tất cả phụ tùng sắp hết hàng tại center này
            List<Inventory> lowStockItems = inventoryRepository.findLowStockPartsByCenter(center.getId());

            if (!lowStockItems.isEmpty()) {
                // Nếu có ít nhất một món sắp hết hàng, gửi email cho tất cả người nhận
                for (AppUser recipient : recipients) {
                    emailService.sendLowStockReportEmail(recipient, center, lowStockItems);
                }
            }
        }

        System.out.println("Daily low stock report job finished.");
    }
}
