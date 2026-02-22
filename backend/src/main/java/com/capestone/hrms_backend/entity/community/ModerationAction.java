package com.capestone.hrms_backend.entity.community;


import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "moderation_actions",
        indexes = {
                @Index(name = "idx_mod_target", columnList = "targetType, targetId"),
                @Index(name = "idx_mod_actor_created", columnList = "actor_id, createdAt DESC")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // POST or COMMENT
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private ModerationTarget targetType;

    // ID of the post or comment (polymorphic)
    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private ModerationActionType action;

    // HR/Moderator performing the action
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private Employee actor;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String reason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
