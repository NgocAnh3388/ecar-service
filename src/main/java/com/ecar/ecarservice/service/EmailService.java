package com.ecar.ecarservice.service;

import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // Để xử lý template Thymeleaf

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async // Gửi mail bất đồng bộ để không làm chậm request chính
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            AppUser user = booking.getUser();
            // --- LOGIC LẤY TÊN ---
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            Context context = new Context();
            context.setVariable("customerName", customerName); // Hoặc lấy tên nếu có
            context.setVariable("licensePlate", booking.getLicensePlate());
            context.setVariable("carModel", booking.getCarModel());
            context.setVariable("serviceCenter", booking.getServiceCenter());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd-MM-yyyy");
            context.setVariable("appointmentTime", booking.getAppointmentDateTime().format(formatter));

            String htmlContent = templateEngine.process("booking-confirmation-email", context);

            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Xác nhận yêu cầu đặt lịch", htmlContent);
        } catch (Exception e) {
            // Log lỗi ở đây
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }

//    @Async
//    public void sendDateBasedMaintenanceReminderEmail(AppUser user, String licensePlate, LocalDate nextDueDate, List<MaintenanceScheduleDto> maintenanceItems) {
//        try {
//            // --- LOGIC LẤY TÊN ---
//            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
//                    ? user.getFullName()
//                    : user.getEmail();
//
//            Context context = new Context();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//            // Truyền tên đã xử lý vào template
//            context.setVariable("customerName", customerName);
//            context.setVariable("licensePlate", licensePlate);
//            context.setVariable("nextDueDate", nextDueDate.format(formatter));
//            // Tính ngày bảo dưỡng cuối từ ngày đáo hạn
//            context.setVariable("lastServiceDate", nextDueDate.minusMonths(12).format(formatter));
//            context.setVariable("maintenanceItems", maintenanceItems); // Truyền nguyên list DTO vào
//
//            String htmlContent = templateEngine.process("date-reminder-email", context);
//
//            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Nhắc nhở bảo dưỡng", htmlContent);
//        } catch (Exception e) {
//            System.err.println("Failed to send date-based maintenance reminder email: " + e.getMessage());
//        }
//    }

}
