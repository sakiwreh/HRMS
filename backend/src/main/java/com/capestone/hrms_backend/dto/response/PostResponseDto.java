package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.community.Visibility;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private Long id;

    private ActorDto author;

    private String title;
    private String description;

    private Visibility visibility;
    private boolean systemGenerated;

    private int likeCount;
    private int commentCount;

    private List<TagDto> tags;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // For soft delete awareness on admin screens (omit for public feed if you prefer)
    private OffsetDateTime deletedAt;

}
