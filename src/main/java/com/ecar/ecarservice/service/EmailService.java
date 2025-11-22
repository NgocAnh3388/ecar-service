package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Center;
import com.ecar.ecarservice.entities.Inventory;
import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.entities.Vehicle;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /** ------------------- BOOKING CONFIRMATION (using BookingRequest) ------------------- */
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



    /** ------------------- LOW STOCK ALERT (New method) ------------------- */
    @Async // Run asynchronously to avoid slowing the main thread
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
            // Log error instead of crashing the application
            System.err.println("Failed to send low stock alert email to " + recipient.getEmail() + ": " + e.getMessage());
        }
    }

    /** ------------------- DAILY LOW STOCK REPORT (New method) ------------------- */
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

    // --- NEW METHOD: Send email requesting customer to approve additional cost ---
    @Async
    public void sendAdditionalCostApprovalRequestEmail(AppUser owner, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("customerName", owner.getFullName());
            context.setVariable("licensePlate", ticket.getVehicle().getLicensePlate());
            context.setVariable("reason", ticket.getAdditionalCostReason());
            // Format amount nicely
            String formattedAmount = String.format("%,.0f VND", ticket.getAdditionalCostAmount());
            context.setVariable("amount", formattedAmount);

            String htmlContent = templateEngine.process("additional-cost-approval-request", context);
            sendHtmlEmail(owner.getEmail(), "[Ecar Service] Request for Additional Cost Approval for Ticket #" + ticket.getId(), htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send additional cost approval request email: " + e.getMessage());
        }
    }

    // --- OPTIONAL NEW METHOD: Notify staff when customer approves ---
    @Async
    public void sendCostApprovedNotificationToStaff(AppUser staff, MaintenanceHistory ticket) {
        try {
            Context context = new Context();
            context.setVariable("customerName", ticket.getOwner().getFullName());
            String formattedAmount = String.format("%,.0f VND", ticket.getAdditionalCostAmount());
            context.setVariable("amount", formattedAmount);
            context.setVariable("ticketId", ticket.getId());

            String htmlContent = templateEngine.process("cost-approved-notification", context);
            sendHtmlEmail(staff.getEmail(), "[Notification] Customer has approved cost for Ticket #" + ticket.getId(), htmlContent);
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


}
