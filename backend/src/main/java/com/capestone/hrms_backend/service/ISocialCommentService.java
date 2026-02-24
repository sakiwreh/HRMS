package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.social.request.SocialCommentRequest;
import com.capestone.hrms_backend.dto.social.request.SocialCommentRequest;
import com.capestone.hrms_backend.dto.social.response.SocialCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISocialCommentService {
    Page<SocialCommentResponse> listComments(Long postId, Pageable pageable);
    SocialCommentResponse addComment(Long postId, Long actorEmployeeId, SocialCommentRequest request);
    SocialCommentResponse updateComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, SocialCommentRequest request);
    void deleteComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, String reason);
}