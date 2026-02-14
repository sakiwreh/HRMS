package com.capestone.hrms_backend.repository.referral;

import com.capestone.hrms_backend.entity.job.JobReferral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobReferralRepository extends JpaRepository<JobReferral,Long> {
}
