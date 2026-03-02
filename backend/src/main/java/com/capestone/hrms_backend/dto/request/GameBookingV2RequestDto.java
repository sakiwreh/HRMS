package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GameBookingV2RequestDto {
    @NotNull(message = "slotStart is required")
    private LocalDateTime slotStart;

    @NotEmpty(message = "At least one participant is required")
    private List<Long> participantIds;
}
