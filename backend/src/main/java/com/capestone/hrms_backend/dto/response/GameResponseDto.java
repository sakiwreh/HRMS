package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class GameResponseDto {
    private Long id;
    private String name;
    private boolean active;
    private LocalTime startHour;
    private LocalTime endHour;
    private int maxDurationMins;
    private int maxPlayersPerSlot;
    private int cancellationBeforeMins;
}
