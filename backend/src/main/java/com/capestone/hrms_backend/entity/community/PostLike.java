package com.capestone.hrms_backend.entity.community;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.mapping.Join;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Getter
@Setter
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private AchievementPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liked_by")
    private Employee likedBy;

    @CreationTimestamp
    @Column(name = "liked_at")
    private LocalDateTime likeadAt;
}
