package com.capestone.hrms_backend.entity.social;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "social_post_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_social_like_post_employee", columnNames = {"post_id", "employee_id"}),
        indexes = {
                @Index(name = "idx_social_likes_post", columnList = "post_id"),
                @Index(name = "idx_social_likes_employee", columnList = "employee_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SocialPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private SocialPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}