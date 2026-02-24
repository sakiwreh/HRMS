package com.capestone.hrms_backend.entity.social;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "social_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_social_tag_name", columnNames = "name"),
        indexes = @Index(name = "idx_social_tag_name", columnList = "name")
)
@Getter
@Setter
@NoArgsConstructor
public class SocialTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;
}