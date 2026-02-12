package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewExpenseRequestDto {
    @NotNull
    private Boolean approved;

    private String remarks;
}
