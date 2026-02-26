package com.capestone.hrms_backend.dto.social.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SocialPostImageContent {
    private String fileName;
    private String fileType;
    private byte[] content;
}
