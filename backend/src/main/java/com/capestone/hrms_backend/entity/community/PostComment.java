package com.capestone.hrms_backend.entity.community;


import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "post_comments",
        indexes = {
                @Index(name = "idx_comment_post_created", columnList = "post_id, createdAt DESC"),
                @Index(name = "idx_comment_author", columnList = "author_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostComment extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // parent post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private AchievementPost post;

    // comment author
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String text;

    @Column
    private OffsetDateTime deletedAt;
}
