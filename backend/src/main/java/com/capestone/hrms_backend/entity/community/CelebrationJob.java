package com.capestone.hrms_backend.entity.community;


import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "celebration_jobs",
        uniqueConstraints = @UniqueConstraint(name = "uk_celebration_unique", columnNames = {"type","employee_id","targetDate"}),
        indexes = @Index(name = "idx_celebration_status_date", columnList = "status, targetDate"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CelebrationJob extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private CelebrationType type; // BIRTHDAY or ANNIVERSARY

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // the date (for current year) this celebration applies to
    @Column(nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private CelebrationJobStatus status = CelebrationJobStatus.PENDING;

    // link to the auto-created system post (if successful)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private AchievementPost post;

}
