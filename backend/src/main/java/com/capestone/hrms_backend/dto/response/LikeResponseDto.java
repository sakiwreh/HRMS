package com.capestone.hrms_backend.dto.response;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {

    private Long id;
    private Long postId;
    private ActorDto employee;         // who liked
    private OffsetDateTime createdAt;

}
