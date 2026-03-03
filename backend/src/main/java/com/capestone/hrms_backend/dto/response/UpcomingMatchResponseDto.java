package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class UpcomingMatchResponseDto {
    private String gameName;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private String bookedByName;
    private List<String> participantNames;
}
