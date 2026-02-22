package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExpenseRequestDto {
    @NotNull
    private Long travelId;

    @NotNull
    private Long categoryId;

    @NotNull
    private BigDecimal amount;

    private String description;

    @NotNull
    private LocalDateTime expenseDate;
    private Boolean draft;
}
