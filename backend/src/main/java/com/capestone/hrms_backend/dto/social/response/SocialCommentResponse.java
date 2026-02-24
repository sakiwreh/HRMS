package com.capestone.hrms_backend.dto.social.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SocialCommentResponse {
    private Long id;
    private Long postId;
    private SocialActorResponse author;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
