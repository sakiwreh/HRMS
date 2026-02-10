package com.capestone.hrms_backend.entity.expenses;

import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;

@Entity
@Table(name = "expenses")
public class ExpenseProof{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_path")
    private String filePath;
}
