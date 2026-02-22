package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.community.ModerationActionType;
import com.capestone.hrms_backend.entity.community.ModerationTarget;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationResponseDto {

    private Long id;

    private ModerationTarget targetType;
    private Long targetId;

    private ModerationActionType action;
    private ActorDto actor;         // HR performing the action

    private String reason;
    private OffsetDateTime createdAt;

}
