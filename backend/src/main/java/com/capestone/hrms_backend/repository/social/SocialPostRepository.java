package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.social.SocialPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {
    @EntityGraph(attributePaths = {"author", "tags"})
    Optional<SocialPost> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("""
           SELECT DISTINCT p FROM SocialPost p
           LEFT JOIN p.tags t
           WHERE p.deletedAt IS NULL
             AND (:authorId IS NULL OR p.author.id = :authorId)
             AND (:tag IS NULL OR LOWER(t.name) = LOWER(:tag))
             AND (:fromDate IS NULL OR p.createdAt >= :fromDate)
             AND (:toDate IS NULL OR p.createdAt <= :toDate)
           """)
    Page<SocialPost> findFeed(
            @Param("authorId") Long authorId,
            @Param("tag") String tag,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
