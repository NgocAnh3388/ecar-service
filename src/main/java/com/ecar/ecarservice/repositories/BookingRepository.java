package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Tìm các booking của một user cụ thể
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdOrderByAppointmentDateTimeDesc(Long userId);
    List<Booking> findAllByOrderByAppointmentDateTimeDesc();
    // Phương thức mới để tải booking kèm thông tin Center
    @Query("SELECT b FROM Booking b JOIN FETCH b.center WHERE b.id = :id")
    Optional<Booking> findByIdWithCenter(Long id);

}
