package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank(message = "Email cannot be blank,")
    @Email(message = "Please check email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;
}
