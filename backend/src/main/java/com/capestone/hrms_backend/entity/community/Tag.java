package com.capestone.hrms_backend.entity.community;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_tag_name", columnNames = "name"),
        indexes = @Index(name = "idx_tag_name", columnList = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String name;
}
