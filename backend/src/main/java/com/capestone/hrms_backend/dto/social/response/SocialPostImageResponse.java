package com.capestone.hrms_backend.dto.social.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SocialPostImageResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String url;
}
