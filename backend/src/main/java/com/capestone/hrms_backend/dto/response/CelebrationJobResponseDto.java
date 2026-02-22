package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.community.CelebrationJobStatus;
import com.capestone.hrms_backend.entity.community.CelebrationType;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CelebrationJobResponseDto {

    private Long id;

    private CelebrationType type;           // BIRTHDAY / ANNIVERSARY
    private ActorDto employee;              // celebrant
    private LocalDate targetDate;

    private CelebrationJobStatus status;    // PENDING / POSTED / SKIPPED / FAILED
    private Long postId;                    // link to system-generated post, if any

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
