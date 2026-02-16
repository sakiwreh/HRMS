package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobshareRequestDto {
    @Email(message = "Enter valid email format")
    @NotNull(message = "Email is required")
    private String candidateEmail;
}
