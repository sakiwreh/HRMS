package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.community.Tag;
import com.capestone.hrms_backend.entity.community.Visibility;
import com.capestone.hrms_backend.entity.organization.Employee;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private Long id;

    private Long author_id;

    private String title;
    private String description;

    private Visibility visibility;
    private boolean systemGenerated;

    private int likeCount;
    private int commentCount;

    private List<Tag> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For soft delete awareness on admin screens (omit for public feed if you prefer)
    private LocalDateTime deletedAt;

}
