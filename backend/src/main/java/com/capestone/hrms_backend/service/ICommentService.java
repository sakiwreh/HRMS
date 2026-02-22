package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.CommentCreateDto;
import com.capestone.hrms_backend.dto.request.CommentUpdateDto;
import com.capestone.hrms_backend.dto.response.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICommentService {

    CommentResponseDto addComment(Long postId, Long authorEmployeeId, CommentCreateDto dto);

    CommentResponseDto updateComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, CommentUpdateDto dto);

    void softDeleteOwnComment(Long commentId, Long authorEmployeeId);

    void softDeleteByHr(Long commentId, Long hrEmployeeId, String reason);

    Page<CommentResponseDto> listComments(Long postId, Pageable pageable);

}
