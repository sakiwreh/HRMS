package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GameWaitlistResponseDto {
    private Long id;
    private Long gameId;
    private Long slotId;
    private String gameName;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private String requestedByName;
    private LocalDateTime appliedDateTime;
    private String status;
    private int priorityScore;
    private List<String> participantNames;
}
