package com.capestone.hrms_backend.repository.job;

import com.capestone.hrms_backend.entity.job.JobOpening;
import com.capestone.hrms_backend.entity.job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JobOpeningRepository extends JpaRepository<JobOpening,Long> {
    List<JobOpening> findByStatus(JobStatus status);
}
