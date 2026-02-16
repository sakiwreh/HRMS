package com.capestone.hrms_backend.repository.job;

import com.capestone.hrms_backend.entity.job.JobCvReviewer;
import com.capestone.hrms_backend.entity.job.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobCvReviewerRepository extends JpaRepository<JobCvReviewer,Long> {
    List<JobCvReviewer> findByJobId(Long jobId);
    Optional<JobCvReviewer> findByJobIdAndReveiwerId(Long jobId, Long empId);
    List<JobCvReviewer> findByReveiwerId(Long id);
    void deleteByJobIdAndReveiwerId(Long jobId, Long empId);
    Long job(JobOpening job);
}
