package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GameBookingResponseDto {
    private Long id;
    private Long gameId;
    private String gameName;
    private Long slotId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private String bookedByName;
    private LocalDateTime bookingDateTime;
    private String status;
    private List<String> participantNames;
}
