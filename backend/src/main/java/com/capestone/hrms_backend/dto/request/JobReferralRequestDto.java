package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class JobReferralRequestDto {

    @NotBlank(message = "Candidate name is required.")
    private String candidateFullName;

    @NotBlank(message = "Candidate email is required")
    @Email(message = "Email format not valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String candidatePhoneNumber;

    private String notes;
    private MultipartFile file;
}
