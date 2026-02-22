package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.job.ReferralStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobReferralResponseDto {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String candidateFullName;
    private String email;
    private ReferralStatus status;
    private String candidatePhoneNumber;
    private String referrerName;
    private String notes;
}
