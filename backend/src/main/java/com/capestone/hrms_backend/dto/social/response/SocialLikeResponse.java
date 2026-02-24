package com.capestone.hrms_backend.dto.social.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialLikeResponse {
    private Long postId;
    private int Likecount;
    private boolean liked;
}
