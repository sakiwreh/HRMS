package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TravelPlanResponseDto {
    private Long id;
    private String title;
    private String destination;
    private boolean cancelled;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private LocalDateTime createdAt;
    private String createdBy;
}
