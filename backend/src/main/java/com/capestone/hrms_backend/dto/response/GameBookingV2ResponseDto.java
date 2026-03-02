package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GameBookingV2ResponseDto {
    private Long id;
    private Long gameId;
    private String gameName;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private String bookedByName;
    private LocalDateTime bookingDateTime;
    private int priorityScore;
    private String status;
    private List<String> participantNames;
}