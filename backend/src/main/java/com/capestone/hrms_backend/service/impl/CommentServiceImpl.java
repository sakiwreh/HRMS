package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.CommentCreateDto;
import com.capestone.hrms_backend.dto.request.CommentUpdateDto;
import com.capestone.hrms_backend.dto.response.CommentResponseDto;
import com.capestone.hrms_backend.entity.community.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.community.AchievementPostRepository;
import com.capestone.hrms_backend.repository.community.ModerationActionRepository;
import com.capestone.hrms_backend.repository.community.PostCommentRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.ICommentService;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.INotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final AchievementPostRepository postRepo;
    private final PostCommentRepository commentRepo;
    private final ModerationActionRepository moderationRepo;
    private final EmployeeRepository employeeRepo;
    private final ModelMapper modelMapper;
    private final IEmailService emailService;
    private final INotificationService notificationService;

    private AchievementPost getActivePost(Long postId) {
        return postRepo.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found or deleted: " + postId));
    }

    private PostComment getActiveComment(Long commentId) {
        return commentRepo.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found or deleted: " + commentId));
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long postId, Long authorEmployeeId, CommentCreateDto dto) {
        AchievementPost post = getActivePost(postId);
        Employee author = employeeRepo.getReferenceById(authorEmployeeId);

        PostComment c = modelMapper.map(dto, PostComment.class);
        c.setPost(post);
        c.setAuthor(author);

        c = commentRepo.save(c);
        postRepo.incrementCommentCount(postId);

        // Notify post author if different
        if (!Objects.equals(post.getAuthor().getId(), authorEmployeeId)) {
            notificationService.create(post.getAuthor().getId(),
                    "New comment on your post",
                    author.getFirstName() + " commented on your post \"" + post.getTitle() + "\"");
        }

        return modelMapper.map(c, CommentResponseDto.class);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long actorEmployeeId, boolean actorIsHr, CommentUpdateDto dto) {
        PostComment c = getActiveComment(commentId);
        if (!actorIsHr && !Objects.equals(c.getAuthor().getId(), actorEmployeeId)) {
            throw new SecurityException("Not allowed to edit this comment");
        }
        modelMapper.map(dto, c);
        return modelMapper.map(c, CommentResponseDto.class);
    }

    @Override
    @Transactional
    public void softDeleteOwnComment(Long commentId, Long authorEmployeeId) {
        PostComment c = getActiveComment(commentId);
        if (!Objects.equals(c.getAuthor().getId(), authorEmployeeId)) {
            throw new SecurityException("Not allowed to delete this comment");
        }
        int updated = commentRepo.softDelete(commentId);
        if (updated > 0) {
            postRepo.decrementCommentCount(c.getPost().getId());
        }
    }

    @Override
    @Transactional
    public void softDeleteByHr(Long commentId, Long hrEmployeeId, String reason) {
        PostComment c = getActiveComment(commentId);
        int updated = commentRepo.softDelete(commentId);
        if (updated == 0) return;

        postRepo.decrementCommentCount(c.getPost().getId());

        Employee hr = employeeRepo.getReferenceById(hrEmployeeId);
        moderationRepo.save(ModerationAction.builder()
                .targetType(ModerationTarget.COMMENT)
                .targetId(commentId)
                .action(ModerationActionType.DELETE)
                .actor(hr)
                .reason(reason)
                .build());

        // Notify comment author
        emailService.send(
                c.getAuthor().getUser().getEmail(),
                "Your comment was removed",
                """
                Hi %s,
                
                Your comment on the post "%s" was removed by HR for the following reason:
                %s
                """.formatted(c.getAuthor().getFirstName(), c.getPost().getTitle(), reason)
        );

        notificationService.create(
                c.getAuthor().getId(),
                "Comment removed by HR",
                "A comment you posted was removed. Reason: " + reason
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> listComments(Long postId, Pageable pageable) {
        AchievementPost post = getActivePost(postId);
        var list = commentRepo.findActiveByPost(post, pageable);
        return new PageImpl<>(list.stream().map(c -> modelMapper.map(c, CommentResponseDto.class)).toList(), pageable, list.size());
    }
}