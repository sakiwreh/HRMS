package com.capestone.hrms_backend.dto.social.request;

import com.capestone.hrms_backend.entity.social.SocialVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class SocialPostUpdateRequest {
    @Size(max = 200)
    private String title;
    private String description;

    private SocialVisibility visibility;

    private Set<String> tags;
}
