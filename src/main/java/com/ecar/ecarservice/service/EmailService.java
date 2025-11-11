package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Center;
import com.ecar.ecarservice.entities.Inventory;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.payload.requests.BookingRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /** ------------------- BOOKING CONFIRMATION (using BookingRequest) ------------------- */
    @Async
    public void sendBookingConfirmationEmail(BookingRequest request) {
        try {
            Context context = new Context();
            context.setVariable("customerName", request.customerName());
            context.setVariable("licensePlate", request.licensePlate());
            context.setVariable("carModel", request.carModelName());
            context.setVariable("serviceCenter", request.centerName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy");
            context.setVariable("appointmentTime", request.scheduledAt().format(formatter));

            String htmlContent = templateEngine.process("booking-confirmation-email", context);
            sendHtmlEmail(request.email(), "Ecar Service Center - Xac nhan dat lich", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    /** ------------------- PAYMENT CONFIRMATION ------------------- */
    @Async
    public void sendPaymentConfirmationEmail(AppUser user, String paymentAmount, String paymentId, String description) {
        try {
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("paymentAmount", paymentAmount);
            context.setVariable("paymentId", paymentId);
            context.setVariable("description", description);
            context.setVariable("paymentDate",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("payment-confirmation-email", context);
            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Xac nhan thanh toan", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send payment confirmation email: " + e.getMessage());
        }
    }

    /** ------------------- TECHNICIAN RECEIVED ------------------- */
    @Async
    public void sendTechnicianReceivedEmail(AppUser owner, AppUser technician, Vehicle vehicle) {
        try {
            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("technicianName", technician.getFullName());
            context.setVariable("licensePlate", vehicle.getLicensePlate());
            context.setVariable("carModel", vehicle.getCarModel().getCarName());
            context.setVariable("receivedAt",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("technician-received-email", context);
            sendHtmlEmail(owner.getEmail(), "Ecar Service Center - Technician da tiep nhan xe", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send technician received email: " + e.getMessage());
        }
    }

    /** ------------------- MAINTENANCE REMINDER ------------------- */
    @Async
    public void sendMaintenanceReminderEmail(AppUser owner, Vehicle vehicle, LocalDate nextDate) {
        try {
            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("carModel", vehicle.getCarModel().getCarName());
            context.setVariable("licensePlate", vehicle.getLicensePlate());
            context.setVariable("nextDate", nextDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            String htmlContent = templateEngine.process("maintenance-reminder-email", context);
            sendHtmlEmail(owner.getEmail(), "Ecar Service Center - Nhac nho bao duong", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send reminder email: " + e.getMessage());
        }
    }
    /** ------------------- ASSIGN TECHNICIAN ------------------- */
    @Async
    public void sendTechnicianAssignedEmail(AppUser technician, MaintenanceHistory maintenanceHistory) {
        try {
            AppUser owner = maintenanceHistory.getOwner();
            Vehicle vehicle = maintenanceHistory.getVehicle();

            if (technician == null || owner == null || vehicle == null || vehicle.getCarModel() == null) {
                System.err.println("Cannot send technician assigned email: missing data.");
                return;
            }

            Context context = new Context();
            context.setVariable("technicianName", technician.getFullName());
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("licensePlate", vehicle.getLicensePlate());
            context.setVariable("carModel", vehicle.getCarModel().getCarName());
            context.setVariable("centerName", maintenanceHistory.getCenter().getCenterName());
            context.setVariable("appointmentTime",
                    LocalDateTime.of(maintenanceHistory.getScheduleDate(), maintenanceHistory.getScheduleTime())
                            .format(DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy")));
            context.setVariable("assignedAt",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("technician-assigned-email", context);
            sendHtmlEmail(technician.getEmail(), "Ecar Service Center - Ban da duoc phan cong", htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send technician assigned email: " + e.getMessage());
        }
    }



    /** ------------------- LOW STOCK ALERT (Phương thức mới) ------------------- */
    @Async // Chạy bất đồng bộ để không làm chậm luồng xử lý chính
    public void sendLowStockAlertEmail(AppUser recipient, Inventory inventoryItem) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipient.getFullName());
            context.setVariable("centerName", inventoryItem.getCenter().getCenterName());
            context.setVariable("partName", inventoryItem.getSparePart().getPartName());
            context.setVariable("partNumber", inventoryItem.getSparePart().getPartNumber());
            context.setVariable("currentStock", inventoryItem.getStockQuantity());
            context.setVariable("minStock", inventoryItem.getMinStockLevel());

            String htmlContent = templateEngine.process("low-stock-alert-email", context);
            sendHtmlEmail(recipient.getEmail(), "[Ecar Service] Low Stock Alert for " + inventoryItem.getSparePart().getPartName(), htmlContent);
        } catch (Exception e) {
            // Ghi log lỗi thay vì để nó làm crash ứng dụng
            System.err.println("Failed to send low stock alert email to " + recipient.getEmail() + ": " + e.getMessage());
        }
    }

    /** ------------------- DAILY LOW STOCK REPORT (Phương thức mới) ------------------- */
    @Async
    public void sendLowStockReportEmail(AppUser recipient, Center center, List<Inventory> lowStockItems) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipient.getFullName());
            context.setVariable("centerName", center.getCenterName());
            context.setVariable("lowStockItems", lowStockItems);

            String htmlContent = templateEngine.process("low-stock-report-email", context);
            String subject = String.format("[Ecar Service] Daily Low Stock Report for %s", center.getCenterName());

            sendHtmlEmail(recipient.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send daily low stock report to " + recipient.getEmail() + ": " + e.getMessage());
        }
    }


    /** ------------------- SEND HTML EMAIL (shared) ------------------- */
    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }


}
