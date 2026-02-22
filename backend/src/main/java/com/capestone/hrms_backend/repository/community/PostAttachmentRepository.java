package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.AchievementPost;
import com.capestone.hrms_backend.entity.community.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<PostAttachment,Long> {
    List<PostAttachment> findByPostOrderByCreatedAtAsc(AchievementPost post);
}
