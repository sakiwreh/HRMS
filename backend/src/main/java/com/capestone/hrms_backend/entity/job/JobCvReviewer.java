package com.capestone.hrms_backend.entity.job;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_cv_reviewers")
@Getter
@Setter
public class JobCvReviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobOpening job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reveiwer_id")
    private Employee reveiwer;
}
