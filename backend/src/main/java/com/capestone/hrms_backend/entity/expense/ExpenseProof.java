package com.capestone.hrms_backend.entity.expense;

import com.capestone.hrms_backend.entity.shared.Document;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expense_proofs")
@Getter
@Setter
public class ExpenseProof extends Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exp_id",nullable = false)
    private Expense expense;
}
