package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseProofResponseDto {
    private Long id;
    private Long expenseId;
    private String description;
    private String fileName;
    private Long fileSize;
    private Long uploadedById;
}
