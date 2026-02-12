package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.ExpenseRequestDto;
import com.capestone.hrms_backend.dto.request.ReviewExpenseRequestDto;
import com.capestone.hrms_backend.dto.response.ExpenseCategoryResponseDto;
import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;

import java.util.List;

public interface IExpenseService {
    ExpenseResponseDto create(Long empId, ExpenseRequestDto requestDto);
    List<ExpenseResponseDto> myExpenses(Long empId);
    List<ExpenseResponseDto> pendingExepnses();
    ExpenseResponseDto review(Long expenseId, Long hrId, ReviewExpenseRequestDto requestDto);
    List<ExpenseCategoryResponseDto> getAllCategories();
}
