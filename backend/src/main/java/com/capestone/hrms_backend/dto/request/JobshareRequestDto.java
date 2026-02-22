package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobshareRequestDto {
    @NotNull(message = "Atleast one email is required")
    private List<@Email(message = "Email format not valid") String> candidateEmails;
}
