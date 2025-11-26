package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.*;
import com.ecar.ecarservice.payload.requests.BookingRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // Get sender email address from application.yml
    @Value("${spring.mail.username}")
    private String fromEmail;


    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /** ------------------- BOOKING CONFIRMATION ------------------- */
    @Async
    public void sendBookingConfirmationEmail(BookingRequest request) {
        try {
            Context context = new Context();
            context.setVariable("customerName", request.customerName());
            context.setVariable("licensePlate", request.licensePlate());
            context.setVariable("carModel", request.carModelName());
            context.setVariable("serviceCenter", request.centerName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy");
            context.setVariable("appointmentTime", request.scheduledAt().format(formatter));

            String htmlContent = templateEngine.process("booking-confirmation-email", context);
            sendHtmlEmail(request.email(), "Ecar Service Center - Booking Confirmation", htmlContent);
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
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("payment-confirmation-email", context);
            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Payment Confirmation", htmlContent);
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
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("technician-received-email", context);
            sendHtmlEmail(owner.getEmail(), "Ecar Service Center - Technician Received Your Vehicle", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send technician received email: " + e.getMessage());
        }
    }


    /** ------------------- TECHNICIAN COMPLETED ------------------- */
    @Async
    public void sendTechnicianCompletedEmail(AppUser owner, AppUser technician, MaintenanceHistory ticket) {
        try {
            if (owner == null || technician == null || ticket == null || ticket.getVehicle() == null) {
                System.err.println("Cannot send technician completed email: missing data.");
                return;
            }

            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("technicianName", technician.getFullName());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());
            context.setVariable("carModel", ticket.getVehicle().getCarModel().getCarName());
            context.setVariable("ticketId", ticket.getId());
            context.setVariable("completedAt",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("technician-completed-email", context);
            sendHtmlEmail(owner.getEmail(),
                    "Ecar Service Center - Your Vehicle Maintenance is Completed",
                    htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send technician completed email: " + e.getMessage());
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
            sendHtmlEmail(owner.getEmail(), "Ecar Service Center - Maintenance Reminder", htmlContent);
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
                            .format(DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy")));
            context.setVariable("assignedAt",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("technician-assigned-email", context);
            sendHtmlEmail(technician.getEmail(), "Ecar Service Center - You Have Been Assigned", htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send technician assigned email: " + e.getMessage());
        }
    }



    /** ------------------- LOW STOCK ALERT ------------------- */
    @Async
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
            System.err.println("Failed to send low stock alert email to " + recipient.getEmail() + ": " + e.getMessage());
        }
    }

    /** ------------------- DAILY LOW STOCK REPORT ------------------- */
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

    // --- ADDITIONAL COST REQUEST (To Customer) ---
    @Async
    public void sendAdditionalCostApprovalRequestEmail(AppUser owner, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());
            context.setVariable("reason", ticket.getAdditionalCostReason());
            String formattedAmount = String.format("%,.0f VND", ticket.getAdditionalCostAmount());
            context.setVariable("amount", formattedAmount);

            String htmlContent = templateEngine.process("additional-cost-approval-request", context);
            sendHtmlEmail(owner.getEmail(), "[Ecar Service] Additional Cost Approval Required - Order #" + ticket.getId(), htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send additional cost approval request email: " + e.getMessage());
        }
    }

    // --- COST APPROVED NOTIFICATION (To Staff) ---
    @Async
    public void sendCostApprovedNotificationToStaff(AppUser staff, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("customerName", ticket.getOwner().getFullName());
            String formattedAmount = String.format("%,.0f VND", ticket.getAdditionalCostAmount());
            context.setVariable("amount", formattedAmount);
            context.setVariable("ticketId", ticket.getId());

            String htmlContent = templateEngine.process("cost-approved-notification", context);
            sendHtmlEmail(staff.getEmail(), "[Notification] Customer has approved additional cost for Order #" + ticket.getId(), htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send cost approved notification email: " + e.getMessage());
        }
    }

    /** ------------------- SEND HTML EMAIL (shared) ------------------- */
    private void sendHtmlEmail(String to, String subject, String htmlBody)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail, "Ecar Service Center");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }

    // --- ADDITIONAL COST REQUEST (To Staff - Internal) ---
    public void sendAdditionalCostRequestEmail(AppUser recipient, MaintenanceHistory ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient.getEmail());
            helper.setSubject("[ECar] Additional Cost Approval Request - Order #" + ticket.getId());

            String htmlContent = "<h3>Hello " + recipient.getFullName() + ",</h3>"
                    + "<p>The technician has reported an additional cost for vehicle: <b>" + ticket.getVehicle().getLicensePlate() + "</b></p>"
                    + "<ul>"
                    + "<li><b>Amount:</b> " + ticket.getAdditionalCostAmount() + " VND</li>"
                    + "<li><b>Reason:</b> " + ticket.getAdditionalCostReason() + "</li>"
                    + "</ul>"
                    + "<p>Please contact the customer and update the status in the system.</p>";

            helper.setText(htmlContent, true); // true = html

            mailSender.send(message);
            System.out.println("Sent additional cost email to " + recipient.getEmail());

        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    // 1. Gửi cho Staff khi Tech báo cáo
    @Async
    public void sendInternalCostAlert(AppUser staff, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("staffName", staff.getFullName());
            context.setVariable("technicianName", ticket.getTechnician().getFullName());
            context.setVariable("ticketId", ticket.getId());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());
            context.setVariable("amount", String.format("%,.0f VND", ticket.getAdditionalCostAmount()));
            context.setVariable("reason", ticket.getAdditionalCostReason());

            String htmlContent = templateEngine.process("additional-cost-request-internal", context);
            sendHtmlEmail(staff.getEmail(), "[ACTION REQUIRED] Technician Reported Cost - Order #" + ticket.getId(), htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send internal cost alert: " + e.getMessage());
        }
    }

    // 2. Gửi cho Khách hàng để xin duyệt (Đã có hàm sendAdditionalCostApprovalRequestEmail, nhớ update template name)
    // Sửa lại tên template trong hàm cũ thành "additional-cost-approval-request"

    // 3. Gửi thông báo Khách đã duyệt (Cho cả Staff và Tech nếu cần)
    @Async
    public void sendApprovalNotification(AppUser recipient, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("recipientName", recipient.getFullName());
            context.setVariable("customerName", ticket.getOwner().getFullName());
            context.setVariable("ticketId", ticket.getId());

            String htmlContent = templateEngine.process("cost-approved-notification", context);
            sendHtmlEmail(recipient.getEmail(), "[APPROVED] Customer Approved Cost - Order #" + ticket.getId(), htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send approval notification: " + e.getMessage());
        }
    }
    // --- NEW: Gửi email thông báo hủy đơn hàng ---
    @Async
    public void sendOrderCancelledEmail(AppUser owner, MaintenanceHistory ticket, String reason) {
        try {
            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("ticketId", ticket.getId());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());
            context.setVariable("reason", reason);
            context.setVariable("cancelledAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));

            // Đảm bảo bạn đã tạo file order-cancelled-email.html trong resources/templates
            String htmlContent = templateEngine.process("order-cancelled-email", context);

            sendHtmlEmail(owner.getEmail(), "[Ecar Service] Order #" + ticket.getId() + " Cancelled", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }

    // =========================================================================
    // --- NEW METHOD: CHO TRƯỜNG HỢP BOOKING (Mới thêm) ---
    // =========================================================================
    @Async
    public void sendOrderCancelledEmail(Booking booking) {
        try {
            Context context = new Context();
            // Xử lý tên khách hàng
            String customerName = (booking.getUser() != null) ? booking.getUser().getFullName() : "Customer";
            context.setVariable("customerName", customerName);
            context.setVariable("bookingId", booking.getId());

            // Format ngày hủy
            String formattedDate = "";
            if (booking.getUpdatedAt() != null) {
                // Booking dùng java.util.Date nên dùng SimpleDateFormat
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                formattedDate = sdf.format(booking.getUpdatedAt());
            } else {
                // Fallback nếu null
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                formattedDate = sdf.format(new Date());
            }
            context.setVariable("cancelDate", formattedDate);

            // Xử lý template
            String htmlContent = templateEngine.process("order-cancelled-email", context);

            // Gửi email
            String emailTo = (booking.getUser() != null) ? booking.getUser().getEmail() : null;
            if (emailTo != null) {
                sendHtmlEmail(emailTo, "Ecar Service - Booking Cancelled", htmlContent);
            } else {
                System.err.println("Cannot send cancellation email: Booking User email is null.");
            }

        } catch (Exception e) {
            System.err.println("Failed to send booking cancellation email: " + e.getMessage());
        }
    }

    // --- [NEW] Gửi email thông báo Tech từ chối việc ---
    @Async
    public void sendTaskDeclinedEmail(AppUser staff, AppUser technician, MaintenanceHistory ticket) {
        try {
            if (staff == null || technician == null) return;

            Context context = new Context();
            context.setVariable("staffName", staff.getFullName());
            context.setVariable("technicianName", technician.getFullName());
            context.setVariable("ticketId", ticket.getId());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());

            String htmlContent = templateEngine.process("task-declined-email", context);

            // Gửi email cho Staff phụ trách
            sendHtmlEmail(staff.getEmail(), "[URGENT] Task Declined - Ticket #" + ticket.getId(), htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send task declined email: " + e.getMessage());
        }
    }
}