package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.social.SocialCelebrationRecord;
import com.capestone.hrms_backend.entity.social.SocialCelebrationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SocialCelebrationRecordRepository extends JpaRepository<SocialCelebrationRecord,Long> {
    boolean existsByEmployeeIdAndCelebrationTypeAndCelebrationDate(
            Long employeeId,
            SocialCelebrationType celebrationType,
            LocalDate celebrationDate
    );
}
