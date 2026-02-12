package com.capestone.hrms_backend.repository.expense;

import com.capestone.hrms_backend.entity.expense.ExpenseProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseProofRepository extends JpaRepository<ExpenseProof,Long> {
     List<ExpenseProof> findByExpenseId(Long expenseId);
}
