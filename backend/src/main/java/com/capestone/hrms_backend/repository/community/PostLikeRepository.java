package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.AchievementPost;
import com.capestone.hrms_backend.entity.community.PostLike;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    boolean existsByPostAndEmployee(AchievementPost post, Employee employee);

    Optional<PostLike> findByPostAndEmployee(AchievementPost post, Employee employee);

    // Who liked a post (for “recent likers” UI)
    @Query("""
           SELECT pl FROM PostLike pl
           JOIN FETCH pl.employee e
           WHERE pl.post = :post
           ORDER BY pl.createdAt DESC
           """)
    List<PostLike> findRecentLikes(@Param("post") AchievementPost post);

    // Bulk delete on post removal (if you ever need hard-delete cleanup)
    @Modifying
    int deleteByPost(AchievementPost post);

}
