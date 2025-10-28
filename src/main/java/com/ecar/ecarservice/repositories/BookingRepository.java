package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.Booking;
import com.ecar.ecarservice.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findAllByStatus(BookingStatus status);
    List<Booking> findByTechnicianIdAndStatusIn(Long technicianId, List<BookingStatus> statuses);
}