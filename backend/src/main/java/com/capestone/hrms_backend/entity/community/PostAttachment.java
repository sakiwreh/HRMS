package com.capestone.hrms_backend.entity.community;


import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "post_attachments",
        indexes = @Index(name = "idx_attachment_post", columnList = "post_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // parent post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private AchievementPost post;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Employee uploadedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
