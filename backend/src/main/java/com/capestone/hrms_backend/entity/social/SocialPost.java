package com.capestone.hrms_backend.entity.social;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "social_posts",
        indexes = {
                @Index(name = "idx_social_posts_created_at", columnList = "created_at"),
                @Index(name = "idx_social_posts_author", columnList = "author_id"),
                @Index(name = "idx_social_posts_deleted_at", columnList = "deleted_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SocialPost extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SocialVisibility visibility = SocialVisibility.ALL;

    @Column(name = "is_system_generated", nullable = false)
    private boolean systemGenerated = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_post_type", length = 32)
    private SocialCelebrationType systemPostType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "social_post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_social_post_tag", columnNames = {"post_id", "tag_id"}),
            indexes = {
                    @Index(name = "idx_social_post_tags_post", columnList = "post_id"),
                    @Index(name = "idx_social_post_tags_tag", columnList = "tag_id")
            }
    )
    private Set<SocialTag> tags = new HashSet<>();
}