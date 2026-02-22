package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.JobReferralRequestDto;
import com.capestone.hrms_backend.dto.request.JobshareRequestDto;
import com.capestone.hrms_backend.dto.response.JobReferralResponseDto;
import com.capestone.hrms_backend.entity.job.JobReferral;
import com.capestone.hrms_backend.entity.job.JobShare;
import com.capestone.hrms_backend.entity.job.ReferralStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.job.JobCvReviewerRepository;
import com.capestone.hrms_backend.repository.job.JobOpeningRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.referral.JobReferralRepository;
import com.capestone.hrms_backend.repository.referral.JobShareRepository;
import com.capestone.hrms_backend.service.IJobReferralService;
import com.capestone.hrms_backend.service.INotificationService;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private final IEmailService emailService;
    private final JobCvReviewerRepository cvReviewerRepository;

    @Override
    public void shareJob(Long jobId, Long empId, JobshareRequestDto dto) {
        Employee sharer = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not present"));
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        String jobTitle = job.getTitle();
        String shareSubject = "Job Opportunity: " + jobTitle;
        String shareBody = String.format(
                "Hi,\n\nCheck out this job opening:\n\nTitle: %s\n\nDescription: %s\n\nExperience Required: %.1f years\n\nShared by an employee at the organization.",
                jobTitle, job.getDescription(), job.getExperienceRequired());
        File jdFile = new File(job.getJobDescriptionUrl());

        for (String email : dto.getCandidateEmails()) {
            JobShare share = new JobShare();
            share.setSharedBy(sharer);
            share.setJob(job);
            share.setCandidateEmail(email);
            jobShareRepository.save(share);

            if (jdFile.exists()) {
                emailService.sendWithAttachment(email, shareSubject, shareBody, jdFile);
            } else {
                emailService.send(email, shareSubject, shareBody);
            }
        }
    }

    @Override
    public void referCandidate(Long jobId, Long empId, JobReferralRequestDto dto) throws IOException {
        JobReferral referral = modelMapper.map(dto, JobReferral.class);
        referral.setReferrer(employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("employee not found")));
        referral.setJob(
                jobRepository.findById(jobId)
                        .orElseThrow(() -> new ResourceNotFoundException("Job not found")));
        String path = fileStorageService.saveCv(jobId, dto.getFile());
        referral.setCandidateCvLink(path);
        referralRepository.save(referral);

        // Notify the job creator (HR) about the new referral
        if (referral.getJob().getCreatedBy() != null) {
            String subject = "New Referral for: " + referral.getJob().getTitle();
            String body = String.format(
                    "%s referred %s for the position \"%s\".",
                    referral.getReferrer().getFirstName() + " "
                            + referral.getReferrer().getLastName(),
                    referral.getCandidateFullName(),
                    referral.getJob().getTitle());
            notificationService.create(referral.getJob().getCreatedBy().getId(), subject, body);

            String referralSubject = "New Referral: " + referral.getCandidateFullName() + " for "
                    + referral.getJob().getTitle();
            String referralBody = String.format(
                    "A new candidate has been referred for the position \"%s\".\n\n" +
                            "Referrer: %s %s\n" +
                            "Candidate: %s\n" +
                            "Email: %s\n" +
                            "Phone: %s\n" +
                            "Notes: %s",
                    referral.getJob().getTitle(),
                    referral.getReferrer().getFirstName(), referral.getReferrer().getLastName(),
                    referral.getCandidateFullName(),
                    referral.getEmail(),
                    referral.getCandidatePhoneNumber() != null ? referral.getCandidatePhoneNumber()
                            : "N/A",
                    referral.getNotes() != null ? referral.getNotes() : "N/A");

            List<String> recipients = new ArrayList<>();
            // Job's communication email (HR contact)
            recipients.add(referral.getJob().getCommunicationEmail());
            // Job creator's email
            if (referral.getJob().getCreatedBy().getUser() != null) {
                recipients.add(referral.getJob().getCreatedBy().getUser().getEmail());
            }
            // CV reviewers' emails (§6.3)
            cvReviewerRepository.findByJobId(jobId).forEach(reviewer -> {
                if (reviewer.getReveiwer() != null && reviewer.getReveiwer().getUser() != null) {
                    recipients.add(reviewer.getReveiwer().getUser().getEmail());
                }
            });

            File cvFile = referral.getCandidateCvLink() != null ? new File(referral.getCandidateCvLink())
                    : null;
            emailService.sendWithAttachment(recipients, referralSubject, referralBody, cvFile);
        }
    }

    @Override
    public List<JobReferralResponseDto> getMyReferrals(Long empId) {
        return referralRepository.findByReferrerId(empId).stream().map(this::toDto).toList();
    }

    @Override
    public List<JobReferralResponseDto> getAllReferrals() {
        return referralRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public void updateReferralStatus(Long refId, ReferralStatus status) {
        JobReferral ref = referralRepository.findById(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found!"));
        ref.setStatus(status);
        referralRepository.save(ref);

        // Notify the referrer about the status change
        String subject = "Referral Status Updated: " + ref.getCandidateFullName();
        String body = String.format(
                "Your referral for %s (position: \"%s\") has been updated to %s.",
                ref.getCandidateFullName(),
                ref.getJob().getTitle(),
                status.name());
        notificationService.create(ref.getReferrer().getId(), subject, body);
    }

    @Override
    public List<JobReferralResponseDto> getReferralsByJob(Long jobId) {
        return referralRepository.findByJobId(jobId).stream().map(this::toDto).toList();
    }

    private JobReferralResponseDto toDto(JobReferral ref) {
        JobReferralResponseDto dto = modelMapper.map(ref, JobReferralResponseDto.class);
        if (ref.getJob() != null) {
            dto.setJobId(ref.getJob().getId());
            dto.setJobTitle(ref.getJob().getTitle());
        }
        if (ref.getReferrer() != null) {
            dto.setReferrerName(ref.getReferrer().getFirstName() + " " + ref.getReferrer().getLastName());
        }
        return dto;
    }
}