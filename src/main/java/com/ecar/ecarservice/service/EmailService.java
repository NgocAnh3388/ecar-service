package com.ecar.ecarservice.service;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.payload.requests.BookingRequest;
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
     * GỬI MAIL XÁC NHẬN ĐẶT LỊCH
     */
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
            sendHtmlEmail(request.email(), "Ecar Service Center - Xac nhan yeu cau dat lich", htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    /**
     * GỬI MAIL XÁC NHẬN THANH TOÁN
     */
    @Async
    public void sendPaymentConfirmationEmail(AppUser user, String paymentAmount, String paymentId, String description) {
        try {
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("paymentAmount", paymentAmount); // "1000.00 USD"
            context.setVariable("paymentId", paymentId);
            context.setVariable("description", description); // "Gia han goi dich vu"
            context.setVariable("paymentDate",
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm 'ngay' dd-MM-yyyy")));

            String htmlContent = templateEngine.process("payment-confirmation-email", context);
            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Xac nhan thanh toan thanh cong", htmlContent);

        } catch (Exception e) {
            System.err.println("Failed to send payment confirmation email: " + e.getMessage());
        }
    }

    /**
     * GỬI MAIL DẠNG HTML
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
