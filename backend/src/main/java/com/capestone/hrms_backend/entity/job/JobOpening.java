package com.capestone.hrms_backend.entity.job;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_openings")
@Getter
@Setter
public class JobOpening extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50)
    private String title;

    @Lob
    @Column(nullable = false,columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",nullable = false)
    private Employee createdBy;

    @Column(name = "experience_required",nullable = false)
    private float experienceRequired;

    @Column(nullable = false,name = "communication_email")
    private String communicationEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    private String reason;

    @Column(name = "job_desc_path",nullable = false)
    private String jobDescriptionUrl;
}
