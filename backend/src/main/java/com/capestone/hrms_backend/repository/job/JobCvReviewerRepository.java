package com.capestone.hrms_backend.repository.job;

import com.capestone.hrms_backend.entity.job.JobCvReviewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobCvReviewerRepository extends JpaRepository<JobCvReviewer,Long> {
    Optional<JobCvReviewer> findByJobIdAndReveiwerId(Long jobId, Long empId);
    List<JobCvReviewer> findByReveiwerId(Long id);
}
