package com.capestone.hrms_backend.dto.social.request;

import com.capestone.hrms_backend.entity.social.SocialVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SocialPostCreateRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    private SocialVisibility visibility;

    private Set<@NotBlank String> tags;
}
