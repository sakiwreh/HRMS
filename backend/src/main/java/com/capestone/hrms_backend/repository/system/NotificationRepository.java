package com.capestone.hrms_backend.repository.system;

import com.capestone.hrms_backend.entity.system.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    long countByEmployeeIdAndIsReadFalse(Long employeeId);
}
