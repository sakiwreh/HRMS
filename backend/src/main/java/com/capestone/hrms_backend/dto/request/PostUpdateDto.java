package com.capestone.hrms_backend.dto.request;

import com.capestone.hrms_backend.entity.community.Visibility;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {

    @Size(max = 200)
    private String title;        // nullable = no change

    private String description;  // nullable = no change

    private Visibility visibility;

    // full replacement list by tag names (null = no change)
    private Set<String> tags;

}
