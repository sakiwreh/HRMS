package com.capestone.hrms_backend.dto.social.response;

import com.capestone.hrms_backend.entity.social.SocialCelebrationType;
import com.capestone.hrms_backend.entity.social.SocialVisibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SocialPostResponse {
    private Long id;
    private SocialActorResponse author;
    private String title;
    private String description;
    private List<String> tags;
    private SocialVisibility visibility;
    private boolean systemGenerated;
    private SocialCelebrationType systemPostType;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SocialCommentResponse> recentComments;
}
