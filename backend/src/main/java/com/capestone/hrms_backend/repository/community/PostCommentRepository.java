package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.AchievementPost;
import com.capestone.hrms_backend.entity.community.PostComment;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment,Long> {

    // Load one (soft-delete aware)
    Optional<PostComment> findByIdAndDeletedAtIsNull(Long id);

    // Comments for a post (reverse chronological)
    @Query("""
           SELECT c FROM PostComment c
           JOIN FETCH c.author a
           WHERE c.post = :post AND c.deletedAt IS NULL
           ORDER BY c.createdAt DESC
           """)
    List<PostComment> findActiveByPost(@Param("post") AchievementPost post, Pageable pageable);

    // Author's own comments (for profile/history screens)
    List<PostComment> findByAuthorAndDeletedAtIsNullOrderByCreatedAtDesc(Employee author, Pageable pageable);

    // Soft delete comment
    @Modifying
    @Query("UPDATE PostComment c SET c.deletedAt = function('SYSDATETIMEOFFSET') WHERE c.id = :id AND c.deletedAt IS NULL")
    int softDelete(@Param("id") Long id);

    // Hard delete all on post removal (if ever needed)
    @Modifying
    int deleteByPost(AchievementPost post);
}
