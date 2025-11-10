package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Tìm các booking của một user cụ thể
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdOrderByAppointmentDateTimeDesc(Long userId);
//    List<Booking> findAllByOrderByAppointmentDateTimeDesc();
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user ORDER BY b.appointmentDateTime DESC")
    List<Booking> findAllByOrderByAppointmentDateTimeDesc();

}
