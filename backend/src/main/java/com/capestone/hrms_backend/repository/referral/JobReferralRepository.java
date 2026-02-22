package com.capestone.hrms_backend.repository.referral;

import com.capestone.hrms_backend.entity.job.JobReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobReferralRepository extends JpaRepository<JobReferral,Long> {
    List<JobReferral> findByReferrerId(Long empId);
    List<JobReferral> findByJobId(Long jobId);
}
