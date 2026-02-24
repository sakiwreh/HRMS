package com.capestone.hrms_backend.repository.social;

import com.capestone.hrms_backend.entity.social.SocialTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Locale;
import java.util.Optional;

public interface SocialTagRepository extends JpaRepository<SocialTag, Long> {
    Optional<SocialTag> findByNameIgnoreCase(String name);
}
