package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TravelPlanRequestDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure date is required")
    @FutureOrPresent(message = "Departure cannot be past date")
    private LocalDateTime departureDate;

    @Min(0)
    private BigDecimal maxPerDayAmount;

    @FutureOrPresent(message = "Return date cannot be past date")
    private LocalDateTime returnDate;
}
