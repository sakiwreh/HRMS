package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.social.SocialPost;
import com.capestone.hrms_backend.entity.social.SocialPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialPostLikeRepository extends JpaRepository<SocialPostLike,Long> {
    Optional<SocialPostLike> findByPostAndEmployee(SocialPost post, Employee employee);
}
