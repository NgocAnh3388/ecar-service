package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.entities.CarModel;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMaintenanceReminderEmail() {
        // Tạo người dùng
        AppUser user = new AppUser();
        user.setFullName("Ngoc Anh");
        user.setEmail("dinhthingocanh030805@gmail.com"); // email thật để test

        // Tạo CarModel
        CarModel carModel = new CarModel();
        carModel.setCarName("Toyota Camry");
        carModel.setCarType("Sedan");

        // Tạo Vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("51A-12345");
        vehicle.setCarModel(carModel);
        vehicle.setOwner(user);

        // Ngày bảo dưỡng sắp tới (10 ngày)
        LocalDate nextDate = LocalDate.now().plusDays(10);

        // Gửi mail nhắc bảo dưỡng
        emailService.sendMaintenanceReminderEmail(user, vehicle, nextDate);

        System.out.println("✅ Mail reminder test đã gửi tới: " + user.getEmail());
    }
}
