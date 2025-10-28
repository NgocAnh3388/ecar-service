package com.ecar.ecarservice.repositories;

import com.ecar.ecarservice.enitiies.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    // Tìm tất cả lịch trình tại một mốc km cụ thể
    List<MaintenanceSchedule> findByKilometerMark(int kilometerMark);

    // Tìm mốc km gần nhất và nhỏ hơn hoặc bằng số km đầu vào
    @Query("SELECT MAX(ms.kilometerMark) FROM MaintenanceSchedule ms WHERE ms.kilometerMark <= ?1")
    Optional<Integer> findClosestKilometerMark(int kilometers);
}