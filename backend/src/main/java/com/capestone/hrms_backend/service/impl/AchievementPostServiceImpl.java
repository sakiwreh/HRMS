package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.PostCreateDto;
import com.capestone.hrms_backend.dto.request.PostUpdateDto;
import com.capestone.hrms_backend.dto.response.ActorDto;
import com.capestone.hrms_backend.dto.response.AttachmentResponseDto;
import com.capestone.hrms_backend.dto.response.LikeResponseDto;
import com.capestone.hrms_backend.dto.response.PostResponseDto;
import com.capestone.hrms_backend.entity.community.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.community.*;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IAchievementPostService;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.INotificationService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementPostServiceImpl implements IAchievementPostService {

    private final AchievementPostRepository postRepo;
    private final TagRepository tagRepo;
    private final PostLikeRepository likeRepo;
    private final PostCommentRepository commentRepo;
    private final PostAttachmentRepository attachmentRepo;
    private final ModerationActionRepository moderationRepo;
    private final EmployeeRepository employeeRepo;

    private final ModelMapper modelMapper;
    private final IEmailService emailService;
    private final INotificationService notificationService;
    private final FileStorageService fileStorageService;

    private AchievementPost getActivePost(Long id) {
        return postRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found or deleted: " + id));
    }

    private Set<Tag> resolveTagsFromNames(Set<String> names) {
        if (names == null) return Set.of();
        Set<String> normalized = names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        Set<Tag> resolved = new HashSet<>();
        for (String n : normalized) {
            tagRepo.findByNameIgnoreCase(n)
                    .ifPresentOrElse(resolved::add, () -> {
                        Tag t = Tag.builder().name(n).build();
                        resolved.add(tagRepo.save(t));
                    });
        }
        return resolved;
    }

    @Override
    @Transactional
    public PostResponseDto createPost(Long authorEmployeeId, PostCreateDto dto) {
        Employee author = employeeRepo.findById(authorEmployeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + authorEmployeeId));

        AchievementPost post = modelMapper.map(dto, AchievementPost.class);
        post.setAuthor(author);
        post.setSystemGenerated(false);
        post.setTags(resolveTagsFromNames(dto.getTags()));

        AchievementPost saved = postRepo.save(post);

        // In-app notification to author (optional)
        notificationService.create(author.getId(),
                "Post created",
                "Your achievement post \"" + saved.getTitle() + "\" has been published.");

        return toPostDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostResponseDto> getPost(Long postId) {
        return postRepo.findByIdAndDeletedAtIsNull(postId).map(this::toPostDto);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> listFeed(Long authorId, String tagName, OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        if (tagName != null && !tagName.isBlank()) {
            return postRepo.findByTagName(tagName.trim(), pageable).map(this::toPostDto);
        }
        if (authorId != null) {
            Employee author = employeeRepo.getReferenceById(authorId);
            return postRepo.findByAuthorAndDeletedAtIsNullOrderByCreatedAtDesc(author, pageable)
                    .map(this::toPostDto);
        }
        if (from != null && to != null) {
            return postRepo.findByDeletedAtIsNullAndCreatedAtBetweenOrderByCreatedAtDesc(from, to, pageable)
                    .map(this::toPostDto);
        }
        return postRepo.findByDeletedAtIsNull(pageable)
                .map(this::toPostDto);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, Long actorEmployeeId, boolean actorIsHr, PostUpdateDto dto) {
        AchievementPost post = getActivePost(postId);

        // RBAC: only author or HR
        if (!actorIsHr && !Objects.equals(post.getAuthor().getId(), actorEmployeeId)) {
            throw new SecurityException("Not allowed to edit this post");
        }

        // PATCH fields
        modelMapper.map(dto, post);

        // tags patch semantics: null = no change; empty = clear; non-empty = replace
        if (dto.getTags() != null) {
            Set<Tag> newTags = dto.getTags().isEmpty()
                    ? new HashSet<>()
                    : resolveTagsFromNames(dto.getTags());
            post.setTags(newTags);
        }
        AchievementPost updated = postRepo.save(post);
        return toPostDto(updated);
    }

    @Override
    @Transactional
    public void softDeleteOwnPost(Long postId, Long authorEmployeeId) {
        AchievementPost post = getActivePost(postId);
        if (!Objects.equals(post.getAuthor().getId(), authorEmployeeId)) {
            throw new SecurityException("Not allowed to delete this post");
        }
        postRepo.softDelete(postId);
    }

    @Override
    @Transactional
    public void softDeleteByHr(Long postId, Long hrEmployeeId, String reason) {
        AchievementPost post = getActivePost(postId);
        int updated = postRepo.softDelete(postId);
        if (updated == 0) return;

        Employee hr = employeeRepo.getReferenceById(hrEmployeeId);
        moderationRepo.save(ModerationAction.builder()
                .targetType(ModerationTarget.POST)
                .targetId(postId)
                .action(ModerationActionType.DELETE)
                .actor(hr)
                .reason(reason)
                .build());

        // Warn author via email + in-app
        emailService.send(
                post.getAuthor().getUser().getEmail(),
                "Your achievement post was removed",
                """
                Hi %s,
                
                Your post "%s" was removed by HR for the following reason:
                %s
                
                If you have questions, please contact HR.
                """.formatted(post.getAuthor().getFirstName(), post.getTitle(), reason)
        );

        notificationService.create(
                post.getAuthor().getId(),
                "Content removed by HR",
                "Your post \"" + post.getTitle() + "\" was removed. Reason: " + reason
        );
    }

    @Override
    @Transactional
    public LikeResponseDto like(Long postId, Long employeeId) {
        AchievementPost post = getActivePost(postId);
        Employee emp = employeeRepo.getReferenceById(employeeId);

        // idempotent like
        return likeRepo.findByPostAndEmployee(post, emp)
                .map(existing -> modelMapper.map(existing, LikeResponseDto.class))
                .orElseGet(() -> {
                    PostLike like = likeRepo.save(PostLike.builder().post(post).employee(emp).build());
                    postRepo.incrementLikeCount(postId);

                    // notify author if liker != author
                    if (!Objects.equals(post.getAuthor().getId(), employeeId)) {
                        notificationService.create(post.getAuthor().getId(),
                                "New like on your post",
                                emp.getFirstName() + " liked your post \"" + post.getTitle() + "\"");
                    }
                    return modelMapper.map(like, LikeResponseDto.class);
                });
    }

    @Override
    @Transactional
    public void unlike(Long postId, Long employeeId) {
        AchievementPost post = getActivePost(postId);
        Employee emp = employeeRepo.getReferenceById(employeeId);
        likeRepo.findByPostAndEmployee(post, emp).ifPresent(l -> {
            likeRepo.delete(l);
            postRepo.decrementLikeCount(postId);
        });
    }

    @Override
    @Transactional
    public AttachmentResponseDto addAttachment(Long postId, Long employeeId, MultipartFile file) throws IOException {
        AchievementPost post = getActivePost(postId);
        if (!Objects.equals(post.getAuthor().getId(), employeeId)) {
            throw new SecurityException("Only the author can add attachments to this post");
        }
        String path = fileStorageService.savePostAttachment(postId, file);

        PostAttachment att = PostAttachment.builder()
                .post(post)
                .fileUrl(path)
                .fileName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .uploadedBy(employeeRepo.getReferenceById(employeeId))
                .build();

        att = attachmentRepo.save(att);
        return modelMapper.map(att, AttachmentResponseDto.class);
    }


    private PostResponseDto toPostDto(AchievementPost post) {
        PostResponseDto dto = modelMapper.map(post, PostResponseDto.class);
        if (post.getAuthor() != null) {
            dto.setAuthor(modelMapper.map(post.getAuthor(), ActorDto.class));
        }
        return dto;
    }

}
