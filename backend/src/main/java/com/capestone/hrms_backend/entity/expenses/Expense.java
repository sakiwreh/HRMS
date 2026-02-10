package com.capestone.hrms_backend.entity.expenses;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import jakarta.persistence.*;
import jdk.jfr.Category;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id")
    private TravelPlan travel;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    private String description;

    private Double amount;

    private String remarks;

    @ManyToOne
    @JoinColumn(name = "reveiwed_by")
    private Employee reviewedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
