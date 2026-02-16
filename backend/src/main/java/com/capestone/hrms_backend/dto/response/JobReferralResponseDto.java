package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.job.ReferralStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobReferralResponseDto {

    private Long id;
    private String candidateFullName;
    private String email;
    private ReferralStatus status;
    private String candidatePhoneNumber;
}
