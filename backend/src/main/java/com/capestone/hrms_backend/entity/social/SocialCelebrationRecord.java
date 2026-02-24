package com.capestone.hrms_backend.entity.social;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "social_celebration_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_social_celebration_employee_type_date",
                columnNames = {"employee_id", "celebration_type", "celebration_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SocialCelebrationRecord extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "celebration_type", nullable = false, length = 32)
    private SocialCelebrationType celebrationType;

    @Column(name = "celebration_date", nullable = false)
    private LocalDate celebrationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private SocialPost post;
}