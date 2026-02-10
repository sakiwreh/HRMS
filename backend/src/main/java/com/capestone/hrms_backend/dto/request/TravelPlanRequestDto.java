package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TravelPlanRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private String destination;

    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    private LocalDateTime returnDate;
}
