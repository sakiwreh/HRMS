package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobOpeningResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long hrId;
    private String communicationEmail;
    private String jobDescriptionUrl;
    private String status;
}
