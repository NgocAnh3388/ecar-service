package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Booking;
import com.ecar.ecarservice.entities.MaintenanceHistory; // <<-- THÊM IMPORT
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Ghi chú: Giữ lại phương thức gốc của bạn để gửi email xác nhận cho Booking.
     */
    @Async
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            AppUser user = booking.getUser();
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("licensePlate", booking.getLicensePlate());
            context.setVariable("carModel", booking.getCarModel());
            context.setVariable("serviceCenter", booking.getServiceCenter());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy");
            context.setVariable("appointmentTime", booking.getAppointmentDateTime().format(formatter));

            String htmlContent = templateEngine.process("booking-confirmation-email", context);

            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Appointment Request Received", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    // =================================================================================
    // GHI CHÚ: BỔ SUNG PHƯƠNG THỨC CÒN THIẾU ĐỂ SỬA LỖI
    // Phương thức này được gọi khi khách hàng tạo một yêu cầu bảo dưỡng/sửa chữa (MaintenanceHistory).
    // =================================================================================
    @Async
    public void sendScheduleConfirmationEmail(MaintenanceHistory history) {
        try {
            AppUser user = history.getOwner();
            if (user == null || user.getEmail() == null) {
                System.err.println("Cannot send email: User info is missing in MaintenanceHistory ID: " + history.getId());
                return;
            }

            // Ghi chú: Lấy tên khách hàng, nếu không có thì dùng email.
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            // Ghi chú: Chuẩn bị dữ liệu để đưa vào template.
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("licensePlate", history.getVehicle().getLicensePlate());
            context.setVariable("carModel", history.getVehicle().getCarModel().getCarName());
            context.setVariable("serviceCenter", history.getCenter().getCenterName());

            // Ghi chú: Định dạng ngày giờ cho dễ đọc.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'on' dd-MM-yyyy");
            String appointmentTime = history.getScheduleDate().atTime(history.getScheduleTime()).format(formatter);
            context.setVariable("appointmentTime", appointmentTime);

            String htmlContent = templateEngine.process("schedule-confirmation-email", context);

            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Service Schedule Request Received", htmlContent);

        } catch (Exception e) {
            System.err.println("Error sending schedule confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ghi chú: Phương thức helper để gửi email HTML.
     */
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