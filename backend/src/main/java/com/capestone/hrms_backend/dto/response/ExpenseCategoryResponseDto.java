package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExpenseCategoryResponseDto {
    private Long id;
    private String name;
    private BigDecimal limit_in_inr;
}
