package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.social.SocialPost;
import com.capestone.hrms_backend.entity.social.SocialPostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialPostCommentRepository extends JpaRepository<SocialPostComment,Long> {
    @EntityGraph(attributePaths = {"author"})
    Optional<SocialPostComment> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"author"})
    Page<SocialPostComment> findByPostAndDeletedAtIsNull(SocialPost post, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    List<SocialPostComment> findTop5ByPostAndDeletedAtIsNullOrderByCreatedAtDesc(SocialPost post);
}
