package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Tìm các booking của một user cụ thể
    List<Booking> findByUserId(Long userId);
}
