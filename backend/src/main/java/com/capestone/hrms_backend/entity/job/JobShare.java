package com.capestone.hrms_backend.entity.job;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_shares")
@Getter
@Setter
public class JobShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private JobOpening job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Employee sharedBy;

    @Column(name = "candidate_email")
    private String candidateEmail;

    @CreationTimestamp
    @Column(name = "shared_at")
    private LocalDateTime sharedAt;
}
