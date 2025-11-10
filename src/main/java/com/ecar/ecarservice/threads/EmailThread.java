package com.ecar.ecarservice.threads;

import com.ecar.ecarservice.payload.requests.BookingRequest;
import com.ecar.ecarservice.service.EmailService;

public class EmailThread implements Runnable {

    private BookingRequest bookingRequest;
    private EmailService emailService;

    public EmailThread(BookingRequest bookingRequest, EmailService emailService) {
        this.emailService = emailService;
        this.bookingRequest = bookingRequest;
    }

    @Override
    public void run() {
        this.emailService.sendBookingConfirmationEmail(bookingRequest);
    }
}

