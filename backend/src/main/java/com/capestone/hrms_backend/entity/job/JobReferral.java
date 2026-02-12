package com.capestone.hrms_backend.entity.job;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_referrals")
@Getter
@Setter
public class JobReferral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id",nullable = false)
    private JobOpening job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id",nullable = false)
    private Employee referrer;

    @Column(name = "canidate_full_name",nullable = false)
    private String candidateFullName;

    @Column(name="candidate_email",nullable = false)
    private String email;

    @Column(name = "cv_link")
    private String candidateCvLink;

    @Column(name = "candidate_phone_number")
    private String candidatePhoneNumber;

    @Enumerated(EnumType.STRING)
    private ReferralStatus status;

    private String notes;
}
