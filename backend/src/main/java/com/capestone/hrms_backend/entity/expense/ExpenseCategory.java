package com.capestone.hrms_backend.entity.expense;

import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_categories")
@Getter
@Setter
public class ExpenseCategory extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 30)
    private String name;

    @Column(precision = 12, scale = 2)
    private BigDecimal limit_in_inr;

    private Boolean active = true;
}
