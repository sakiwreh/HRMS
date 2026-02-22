package com.capestone.hrms_backend.dto.response;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponseDto {

    private Long id;
    private Long postId;

    private String fileUrl;
    private String fileName;
    private String mimeType;

    private ActorDto uploadedBy;
    private OffsetDateTime createdAt;

}
