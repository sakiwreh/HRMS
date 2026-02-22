package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.ModerationAction;
import com.capestone.hrms_backend.entity.community.ModerationTarget;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModerationActionRepository extends JpaRepository<ModerationAction,Long> {

    // All actions for a given target (post or comment), newest first
    List<ModerationAction> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(ModerationTarget targetType, Long targetId);

    // Actions performed by a moderator
    List<ModerationAction> findByActorOrderByCreatedAtDesc(Employee actor, Pageable pageable);

    // Last moderation action for a target
    @Query("""
           SELECT m FROM ModerationAction m
           WHERE m.targetType = :targetType AND m.targetId = :targetId
           ORDER BY m.createdAt DESC
           """)
    List<ModerationAction> findLatestForTarget(@Param("targetType") ModerationTarget targetType,
                                               @Param("targetId") Long targetId,
                                               Pageable pageable);

}
