package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.JobOpeningRequestDto;
import com.capestone.hrms_backend.dto.response.JobOpeningResponseDto;
import com.capestone.hrms_backend.dto.response.JobOpeningReviewerResponseDto;
import com.capestone.hrms_backend.entity.job.JobStatus;

import java.io.IOException;
import java.util.List;

public interface IJobOpeningService {
    JobOpeningResponseDto create(Long hrId,JobOpeningRequestDto dto) throws IOException;
    JobOpeningResponseDto getById(Long id);
    List<JobOpeningResponseDto> getAllOpenings();
    void changeStatus(Long id, JobStatus status, String remarks);
    void addReviewer(Long jobOpeningId, List<Long> empIds);
    void removeReviewer(Long jobOpeningId, Long empId);
    List<JobOpeningReviewerResponseDto> getReviewers(Long jobOpeningId);
}
