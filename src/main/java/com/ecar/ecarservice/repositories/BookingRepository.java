package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Tìm các booking của một user cụ thể
    List<Booking> findByUserId(Long userId);
}