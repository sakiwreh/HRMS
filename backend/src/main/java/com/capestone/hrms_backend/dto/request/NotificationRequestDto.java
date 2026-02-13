package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDto {
    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
