package com.capestone.hrms_backend.dto.social.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialCommentRequest {
    @NotBlank
    private String text;
}
