package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GameSlotResponseDto {
    private Long id;
    private Long gameId;
    private String gameName;
    private LocalDate slotDate;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private int capacity;
    private int bookedCount;
    private String status;
}
