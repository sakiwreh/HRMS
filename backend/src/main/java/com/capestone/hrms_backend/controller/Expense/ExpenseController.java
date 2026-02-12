package com.capestone.hrms_backend.controller.Expense;

import com.capestone.hrms_backend.dto.request.ExpenseRequestDto;
import com.capestone.hrms_backend.dto.request.ReviewExpenseRequestDto;
import com.capestone.hrms_backend.dto.response.ExpenseCategoryResponseDto;
import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final IExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> create(@AuthenticationPrincipal HrmsUserDetails user, @RequestBody ExpenseRequestDto requestDto){
        return ResponseEntity.ok(expenseService.create(user.getEmployeeId(),requestDto));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ExpenseResponseDto>> my(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(expenseService.myExpenses(user.getEmployeeId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseResponseDto>> pending(){
        return ResponseEntity.ok(expenseService.pendingExepnses());
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ExpenseResponseDto> review(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user, @RequestBody ReviewExpenseRequestDto requestDto){
        return ResponseEntity.ok(expenseService.review(id, user.getEmpId(),requestDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategoryResponseDto>> getAll(){
        return ResponseEntity.ok(expenseService.getAllCategories());
    }
}
