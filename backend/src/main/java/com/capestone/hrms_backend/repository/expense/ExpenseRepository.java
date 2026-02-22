package com.capestone.hrms_backend.repository.expense;

import com.capestone.hrms_backend.entity.expense.Expense;
import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByTravelPlanId(Long travelPlanId);
    List<Expense> findByEmployeeId(Long empId);
    List<Expense> findByStatus(ExpenseStatus status);
    List<Expense> findByEmployeeIdAndStatus(Long empId,ExpenseStatus status);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.employee.id = :empId AND e.travelPlan.id = :travelId AND e.expenseDate >= :dayStart AND e.expenseDate < :dayEnd")
    BigDecimal sumAmountByEmployeeAndTravelAndDate(@Param("empId") Long empId, @Param("travelId") Long travelId, @Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e WHERE e.travelPlan.id = :travelId")
    BigDecimal sumAmountByTravelPlanId(@Param("travelId") Long travelId);

    @Query("SELECT e FROM Expense e WHERE (:employeeId IS NULL OR e.employee.id = :employeeId) AND (:status IS NULL OR e.status = :status) AND (:travelId IS NULL OR e.travelPlan.id = :travelId) AND (:fromDate IS NULL OR e.expenseDate >= :fromDate) AND (:toDate IS NULL OR e.expenseDate <= :toDate)")
    List<Expense> findFiltered(@Param("employeeId") Long employeeId, @Param("status") ExpenseStatus status,
                               @Param("travelId") Long travelId, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}