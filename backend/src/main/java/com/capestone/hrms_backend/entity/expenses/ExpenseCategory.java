package com.capestone.hrms_backend.entity.expenses;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_catgeories")
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "limit_in_inr")
    private Long limitInInr;
}
