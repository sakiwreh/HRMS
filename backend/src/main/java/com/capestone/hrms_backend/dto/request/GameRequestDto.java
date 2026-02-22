package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class GameRequestDto {

    @NotBlank(message = "Game name is required")
    private String name;

    @NotNull(message = "Start hour is required")
    private LocalTime startHour;

    @NotNull(message = "End hour is required")
    private LocalTime endHour;

    @NotNull(message = "Slot duration is required")
    private Integer maxDurationMins;

    @NotNull(message = "Max players per slot is required")
    @Min(value = 1, message = "Max players must be at least 1")
    private Integer maxPlayersPerSlot;

    @NotNull(message = "Max participants per booking is required")
    @Min(value = 1,message = "Minimum 1 person required for playing the game")
    private Integer maxParticipantsPerBooking;

    @NotNull(message = "Cancellation lead time is required")
    @Min(value = 0, message = "Cancellation minutes cannot be negative")
    private Integer cancellationBeforeMins;

    @NotNull(message = "Slot generation days is necessary")
    @Min(value = 1, message = "Slot generation days must be at least 1")
    private Integer slotGenerationDays;
}
