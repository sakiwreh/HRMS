package com.capestone.hrms_backend.dto.request;

import com.capestone.hrms_backend.entity.community.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateDto {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    // Optional at creation; default should be ALL in service if null
    private Visibility visibility;

    // New or existing tags by name (client can send normalized list)
    private Set<@NotBlank String> tags;

}
