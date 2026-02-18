package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExpenseResponseDto {
    private Long id;
    private Long employeeId;
    private String category;
    private Double amount;
    private String description;
    private LocalDateTime expenseDate;
    private ExpenseStatus status;
    private Long reviewedBy;
    private String remarks;
}
