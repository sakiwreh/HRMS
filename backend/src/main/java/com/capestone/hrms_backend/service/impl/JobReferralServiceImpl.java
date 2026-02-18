package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.JobReferralRequestDto;
import com.capestone.hrms_backend.dto.request.JobshareRequestDto;
import com.capestone.hrms_backend.dto.response.JobReferralResponseDto;
import com.capestone.hrms_backend.entity.job.JobReferral;
import com.capestone.hrms_backend.entity.job.JobShare;
import com.capestone.hrms_backend.entity.job.ReferralStatus;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.job.JobOpeningRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.referral.JobReferralRepository;
import com.capestone.hrms_backend.repository.referral.JobShareRepository;
import com.capestone.hrms_backend.service.IJobReferralService;
import com.capestone.hrms_backend.service.INotificationService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobReferralServiceImpl implements IJobReferralService {
    private final JobShareRepository jobShareRepository;
    private final JobReferralRepository referralRepository;
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;
    private final JobOpeningRepository jobRepository;
    private final FileStorageService fileStorageService;
    private final INotificationService notificationService;

    @Override
    public void shareJob(Long jobId, Long empId,JobshareRequestDto dto) {
        JobShare share = modelMapper.map(dto, JobShare.class);
        share.setSharedBy(employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee not present")));
        share.setJob(jobRepository.findById(jobId).orElseThrow(()->new ResourceNotFoundException("Job not found")));
        //Send email later from here
        jobShareRepository.save(share);
    }

    @Override
    public void referCandidate(Long jobId,Long empId,JobReferralRequestDto dto) throws IOException {
        JobReferral referral = modelMapper.map(dto, JobReferral.class);
        referral.setReferrer(employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("employee not found")));
        referral.setJob(jobRepository.findById(jobId).orElseThrow(()->new ResourceNotFoundException("Job not found")));
        String path = fileStorageService.saveCv(jobId, dto.getFile());
        referral.setCandidateCvLink(path);
        referralRepository.save(referral);

        //Notify HR about job referral
        if (referral.getJob().getCreatedBy() != null) {
            String subject = "New Referral for: " + referral.getJob().getTitle();
            String body = String.format(
                    "%s referred %s for the position \"%s\".",
                    referral.getReferrer().getFirstName() + " " + referral.getReferrer().getLastName(),
                    referral.getCandidateFullName(),
                    referral.getJob().getTitle());
            notificationService.create(referral.getJob().getCreatedBy().getId(), subject, body);
        }

    }

    @Override
    public List<JobReferralResponseDto> getMyReferrals(Long empId) {
        return referralRepository.findByReferrerId(empId).stream().map(ref->modelMapper.map(ref, JobReferralResponseDto.class)).toList();
    }

    @Override
    public void updateReferralStatus(Long refId, ReferralStatus status) {
        JobReferral ref = referralRepository.findById(refId).orElseThrow(()->new ResourceNotFoundException("Referral not found!"));
        ref.setStatus(status);
        referralRepository.save(ref);

        //Notify employee about status change in referral
        String subject = "Referral Status Updated: " + ref.getCandidateFullName();
        String body = String.format(
                "Your referral for %s (position: \"%s\") has been updated to %s.",
                ref.getCandidateFullName(),
                ref.getJob().getTitle(),
                status.name());
        notificationService.create(ref.getReferrer().getId(), subject, body);

    }
}
