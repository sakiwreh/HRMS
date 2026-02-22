package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.PostCreateDto;
import com.capestone.hrms_backend.dto.request.PostUpdateDto;
import com.capestone.hrms_backend.dto.response.AttachmentResponseDto;
import com.capestone.hrms_backend.dto.response.LikeResponseDto;
import com.capestone.hrms_backend.dto.response.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface IAchievementPostService {

    PostResponseDto createPost(Long authorEmployeeId, PostCreateDto dto);

    Optional<PostResponseDto> getPost(Long postId);

    Page<PostResponseDto> listFeed(
            Long authorId,
            String tagName,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    );

    PostResponseDto updatePost(Long postId, Long actorEmployeeId, boolean actorIsHr, PostUpdateDto dto);

    void softDeleteOwnPost(Long postId, Long authorEmployeeId);

    void softDeleteByHr(Long postId, Long hrEmployeeId, String reason);

    LikeResponseDto like(Long postId, Long employeeId);

    void unlike(Long postId, Long employeeId);

    AttachmentResponseDto addAttachment(Long postId, Long employeeId, MultipartFile file) throws IOException;

}
