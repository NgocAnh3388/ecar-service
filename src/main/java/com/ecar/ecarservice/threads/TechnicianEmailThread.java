package com.ecar.ecarservice.threads;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.service.EmailService;

public class TechnicianEmailThread implements Runnable {

    private EmailService emailService;
    AppUser owner;
    AppUser technician;
    Vehicle vehicle;

    public TechnicianEmailThread(EmailService emailService, AppUser owner, AppUser technician, Vehicle vehicle) {
        this.emailService = emailService;
        this.owner = owner;
        this.technician = technician;
        this.vehicle = vehicle;
    }

    @Override
    public void run() {
        emailService.sendTechnicianReceivedEmail(owner, technician, vehicle);

    }
}
