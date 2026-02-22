package com.capestone.hrms_backend.entity.community;


import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "achievement_posts",
        indexes = {
                @Index(name = "idx_post_created_at", columnList = "createdAt DESC"),
                @Index(name = "idx_post_author_id", columnList = "author_id"),
                @Index(name = "idx_post_deleted_at", columnList = "deletedAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementPost extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @NotBlank
    @Column(length = 200, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 32, nullable = false)
    private Visibility visibility = Visibility.ALL; // default required by spec

    @Column(nullable = false)
    private boolean systemGenerated = false; // marks birthday/anniversary posts

    @Column
    private OffsetDateTime deletedAt;

    // cached counters for fast feed rendering
    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    // Many-to-many via a join table with uniqueness on (post_id, tag_id)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_post_tag", columnNames = {"post_id","tag_id"}),
            indexes = {
                    @Index(name = "idx_post_tags_post", columnList = "post_id"),
                    @Index(name = "idx_post_tags_tag", columnList = "tag_id")
            }
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
}
