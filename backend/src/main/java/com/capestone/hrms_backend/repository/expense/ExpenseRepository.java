package com.capestone.hrms_backend.repository.expense;

import com.capestone.hrms_backend.entity.expense.Expense;
import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByTravelPlanId(Long travelPlanId);
    List<Expense> findByEmployeeId(Long empId);
    List<Expense> findByStatus(ExpenseStatus status);

    Long id(Long id);
}