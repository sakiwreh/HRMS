package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.social.request.SocialPostCreateRequest;
import com.capestone.hrms_backend.dto.social.request.SocialPostImageContent;
import com.capestone.hrms_backend.dto.social.request.SocialPostUpdateRequest;
import com.capestone.hrms_backend.dto.social.response.SocialLikeResponse;
import com.capestone.hrms_backend.dto.social.response.SocialPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface ISocialPostService {
//    SocialPostResponse createPost(Long actorEmployeeId, SocialPostCreateRequest request);
    SocialPostResponse getPost(Long postId);
    Page<SocialPostResponse> getFeed(Long authorId, String tag, LocalDateTime from, LocalDateTime to, Pageable pageable);
    SocialPostResponse updatePost(Long postId, Long actorEmployeeId, boolean actorIsHr, SocialPostUpdateRequest request);
    void deletePost(Long postId, Long actorEmployeeId, boolean actorIsHr, String reason);
    SocialLikeResponse likePost(Long postId, Long actorEmployeeId);
    void unlikePost(Long postId, Long actorEmployeeId);
    SocialPostResponse createPost(Long actorEmployeeId, SocialPostCreateRequest request, List<MultipartFile> images);
    SocialPostImageContent getPostImageContent(Long imageId);
}