package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.AchievementPost;
import com.capestone.hrms_backend.entity.community.Visibility;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementPostRepository extends JpaRepository<AchievementPost,Long>, JpaSpecificationExecutor<AchievementPost> {

    // Basic load
    Optional<AchievementPost> findByIdAndDeletedAtIsNull(Long id);

    // Feed: reverse chronological, only non-deleted
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<AchievementPost> findByDeletedAtIsNull(Pageable pageable);

    // Filter: by author
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<AchievementPost> findByAuthorAndDeletedAtIsNullOrderByCreatedAtDesc(Employee author, Pageable pageable);

    // Filter: by date range
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<AchievementPost> findByDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtDesc(
            OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    // Filter: by visibility (if you enable more scopes later)
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<AchievementPost> findByDeletedAtIsNullAndVisibilityOrderByCreatedAtDesc(
            Visibility visibility, Pageable pageable);

    // Filter: by tag name (join)
    @Query("""
           SELECT p FROM AchievementPost p
           JOIN p.tags t
           WHERE p.deletedAt IS NULL
             AND t.name = :tagName
           ORDER BY p.createdAt DESC
           """)
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<AchievementPost> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    // Increment/decrement counters (service should call save, but if you prefer one-shot updates):
    @Modifying
    @Query("UPDATE AchievementPost p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    int incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE AchievementPost p SET p.likeCount = CASE WHEN p.likeCount > 0 THEN p.likeCount - 1 ELSE 0 END WHERE p.id = :postId")
    int decrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE AchievementPost p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    int incrementCommentCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE AchievementPost p SET p.commentCount = CASE WHEN p.commentCount > 0 THEN p.commentCount - 1 ELSE 0 END WHERE p.id = :postId")
    int decrementCommentCount(@Param("postId") Long postId);

    // Soft delete
    @Modifying
    @Query("UPDATE AchievementPost p SET p.deletedAt = function('SYSDATETIMEOFFSET') WHERE p.id = :postId AND p.deletedAt IS NULL")
    int softDelete(@Param("postId") Long postId);

    // Recent posts by IDs (for assembling a specific view)
    @EntityGraph(attributePaths = {"author", "tags"})
    List<AchievementPost> findByIdInAndDeletedAtIsNullOrderByCreatedAtDesc(List<Long> ids);

}
