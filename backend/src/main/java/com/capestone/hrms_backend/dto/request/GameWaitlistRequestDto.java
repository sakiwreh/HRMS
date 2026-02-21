package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameWaitlistRequestDto {
    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotEmpty(message = "At least one participant is required")
    private List<Long> participantIds;
}
