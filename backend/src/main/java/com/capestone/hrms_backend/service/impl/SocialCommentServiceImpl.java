package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.social.request.SocialCommentRequest;
import com.capestone.hrms_backend.dto.social.response.SocialActorResponse;
import com.capestone.hrms_backend.dto.social.response.SocialCommentResponse;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.social.SocialPost;
import com.capestone.hrms_backend.entity.social.SocialPostComment;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.social.SocialPostCommentRepository;
import com.capestone.hrms_backend.repository.social.SocialPostRepository;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.ISocialCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SocialCommentServiceImpl implements ISocialCommentService {

    private final SocialPostRepository postRepository;
    private final SocialPostCommentRepository commentRepository;
    private final EmployeeRepository employeeRepository;
    private final IEmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public Page<SocialCommentResponse> listComments(Long postId, Pageable pageable) {
        SocialPost post = getActivePost(postId);
        return commentRepository.findByPostAndDeletedAtIsNull(post, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public SocialCommentResponse addComment(Long postId, Long actorEmployeeId, SocialCommentRequest request) {
        SocialPost post = getActivePost(postId);
        Employee actor = getEmployee(actorEmployeeId);

        String text = request.getText().trim();
        if (text.isBlank()) {
            throw new BusinessException("Comment cannot be blank");
        }

        SocialPostComment comment = new SocialPostComment();
        comment.setPost(post);
        comment.setAuthor(actor);
        comment.setText(text);

        SocialPostComment saved = commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public SocialCommentResponse updateComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, SocialCommentRequest request) {
        SocialPostComment comment = getActiveComment(commentId);
        if (!actorIsHr && !Objects.equals(comment.getAuthor().getId(), actorEmployeeId)) {
            throw new BusinessException("You can edit only your own comment");
        }

        String text = request.getText().trim();
        if (text.isBlank()) {
            throw new BusinessException("Comment cannot be blank");
        }

        comment.setText(text);
        return toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, String reason) {
        SocialPostComment comment = getActiveComment(commentId);
        boolean isOwner = Objects.equals(comment.getAuthor().getId(), actorEmployeeId);

        if (!isOwner && !actorIsHr) {
            throw new BusinessException("You can delete only your own comment");
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

        SocialPost post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);

        if (actorIsHr && !isOwner) {
            sendHrDeletionEmail(comment.getAuthor(), post.getTitle(), reason);
        }
    }

    private SocialPost getActivePost(Long postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    private SocialPostComment getActiveComment(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
    }

    private Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    private SocialCommentResponse toResponse(SocialPostComment comment) {
        return SocialCommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .author(toActor(comment.getAuthor()))
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private SocialActorResponse toActor(Employee employee) {
        String firstName = employee.getFirstName() == null ? "" : employee.getFirstName().trim();
        String lastName = employee.getLastName() == null ? "" : employee.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        if (fullName.isBlank()) {
            fullName = "Employee " + employee.getId();
        }
        return new SocialActorResponse(employee.getId(), fullName);
    }

    private void sendHrDeletionEmail(Employee targetEmployee, String postTitle, String reason) {
        if (targetEmployee == null || targetEmployee.getUser() == null || targetEmployee.getUser().getEmail() == null) {
            return;
        }
        String finalReason = (reason == null || reason.isBlank()) ? "Inappropriate content" : reason.trim();
        String body = """
                Your comment on post "%s" was removed by HR.
                Reason: %s
                """.formatted(postTitle, finalReason);
        emailService.send(targetEmployee.getUser().getEmail(), "Your social comment was removed by HR", body);
    }
}