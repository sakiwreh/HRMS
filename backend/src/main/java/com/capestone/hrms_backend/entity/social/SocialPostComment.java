package com.capestone.hrms_backend.entity.social;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "social_post_comments",
        indexes = {
                @Index(name = "idx_social_comments_post", columnList = "post_id"),
                @Index(name = "idx_social_comments_author", columnList = "author_id"),
                @Index(name = "idx_social_comments_deleted_at", columnList = "deleted_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SocialPostComment extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private SocialPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}