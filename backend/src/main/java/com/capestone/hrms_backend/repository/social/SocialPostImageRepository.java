package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.social.SocialPost;
import com.capestone.hrms_backend.entity.social.SocialPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialPostImageRepository extends JpaRepository<SocialPostImage, Long> {
    Optional<SocialPostImage> findByIdAndPostDeletedAtIsNull(Long id);
}
