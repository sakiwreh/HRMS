package com.capestone.hrms_backend.repository.community;

import com.capestone.hrms_backend.entity.community.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    // For autosuggest
    List<Tag> findTop10ByNameStartingWithIgnoreCaseOrderByNameAsc(String prefix);

}
