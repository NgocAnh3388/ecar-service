package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.MaintenanceScheduleDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enitiies.Booking;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${app.support.email}")
    private String supportEmail;

    @Value("${app.support.phone}")
    private String supportPhone;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd-MM-yyyy");
            context.setVariable("appointmentTime", booking.getAppointmentDateTime().format(formatter));

            String htmlContent = templateEngine.process("booking-confirmation-email", context);

            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Appointment Request Received", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    @Async
    public void sendDateBasedMaintenanceReminderEmail(AppUser user, String licensePlate, LocalDate nextDueDate, List<MaintenanceScheduleDto> maintenanceItems) {
        try {
            String customerName = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getEmail();

            Context context = new Context();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            context.setVariable("customerName", customerName);
            context.setVariable("licensePlate", licensePlate);
            context.setVariable("nextDueDate", nextDueDate.format(formatter));
            context.setVariable("lastServiceDate", nextDueDate.minusMonths(12).format(formatter));
            context.setVariable("maintenanceItems", maintenanceItems);

            String htmlContent = templateEngine.process("date-reminder-email", context);

            sendHtmlEmail(user.getEmail(), "Ecar Service Center - Scheduled Maintenance Reminder", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send date-based maintenance reminder email: " + e.getMessage());
        }
    }

    @Async
    public void sendBookingStatusUpdateEmail(Booking booking, String subject, String templateName) {
        try {
            AppUser user = booking.getUser();
            String customerName = user.getFullName() != null ? user.getFullName() : user.getEmail();

            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("booking", booking);

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(user.getEmail(), "Ecar Service Center - " + subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send status update email: " + e.getMessage());
        }
    }

    @Async
    public void sendNewTaskNotificationEmail(AppUser technician, Booking booking) {
        try {
            Context context = new Context();
            context.setVariable("technicianName", technician.getFullName() != null ? technician.getFullName() : technician.getEmail());
            context.setVariable("booking", booking);

            String htmlContent = templateEngine.process("new-task-notification-email", context);
            sendHtmlEmail(technician.getEmail(), "Ecar Service Center - New Task Assigned", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send new task notification email: " + e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailFrom, "Ecar Service Center");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        System.out.println("Email sent to: " + to);
    }

}