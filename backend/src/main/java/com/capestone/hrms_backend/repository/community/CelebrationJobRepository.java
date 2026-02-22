package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.CelebrationJob;
import com.capestone.hrms_backend.entity.community.CelebrationJobStatus;
import com.capestone.hrms_backend.entity.community.CelebrationType;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CelebrationJobRepository extends JpaRepository<CelebrationJob,Long> {

    // Prevent duplicates for (type, employee, date)
    Optional<CelebrationJob> findByTypeAndEmployeeAndTargetDate(CelebrationType type, Employee employee, LocalDate targetDate);

    // Scheduler: fetch today's pending jobs
    List<CelebrationJob> findByStatusAndTargetDate(CelebrationJobStatus status, LocalDate targetDate);

    // Bulk operations for a range (useful for replays or audits)
    List<CelebrationJob> findByTargetDateBetween(LocalDate start, LocalDate end);

    // Update status fast (if you prefer not to load entity)
    @Modifying
    @Query("UPDATE CelebrationJob c SET c.status = :status WHERE c.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") CelebrationJobStatus status);
}
