package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.JobReferralRequestDto;
import com.capestone.hrms_backend.dto.request.JobshareRequestDto;
import com.capestone.hrms_backend.dto.response.JobReferralResponseDto;
import com.capestone.hrms_backend.entity.job.ReferralStatus;

import java.io.IOException;
import java.util.List;

public interface IJobReferralService {
    void shareJob(Long jobId,Long empId, JobshareRequestDto dto);
    void referCandidate(Long jobId, Long empId,JobReferralRequestDto dto) throws IOException;
    List<JobReferralResponseDto> getMyReferrals(Long empId);
    void updateReferralStatus(Long refId, ReferralStatus status);
    List<JobReferralResponseDto> getAllReferrals();
}
