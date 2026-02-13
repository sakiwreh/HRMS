package com.capestone.hrms_backend.dto.request;

import com.capestone.hrms_backend.entity.job.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusRequestDto {
    @NotNull(message = "Please provide status")
    private JobStatus status;

    @NotNull(message = "Reason cannot be null")
    private String reason;

}
