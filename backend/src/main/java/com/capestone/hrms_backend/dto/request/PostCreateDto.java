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

    private Visibility visibility;

    private Set<@NotBlank String> tags;

}
