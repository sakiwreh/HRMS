package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GameSlotV2ResponseDto {
    private Long gameId;
    private String gameName;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private String status;
    private int pendingCount;
}