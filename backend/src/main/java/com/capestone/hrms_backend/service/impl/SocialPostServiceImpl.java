package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.social.request.SocialPostCreateRequest;
import com.capestone.hrms_backend.dto.social.request.SocialPostImageContent;
import com.capestone.hrms_backend.dto.social.request.SocialPostUpdateRequest;
import com.capestone.hrms_backend.dto.social.response.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.social.*;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.social.*;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.ISocialPostService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SocialPostServiceImpl implements ISocialPostService {

    private final SocialPostRepository postRepository;
    private final SocialTagRepository tagRepository;
    private final SocialPostLikeRepository likeRepository;
    private final SocialPostCommentRepository commentRepository;
    private final EmployeeRepository employeeRepository;
    private final IEmailService emailService;
    private final FileStorageService fileStorageService;
    private final SocialPostImageRepository imageRepository;

    private static final int MAX_IMAGES_PER_POST = 10;
    //5 MB
    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L;

//    @Override
//    @Transactional
//    public SocialPostResponse createPost(Long actorEmployeeId, SocialPostCreateRequest request) {
//        Employee author = getEmployee(actorEmployeeId);
//
//        SocialPost post = new SocialPost();
//        post.setAuthor(author);
//        post.setTitle(request.getTitle().trim());
//        post.setDescription(request.getDescription().trim());
//        post.setVisibility(request.getVisibility() == null ? SocialVisibility.ALL : request.getVisibility());
//        post.setTags(resolveTags(request.getTags()));
//
//        SocialPost saved = postRepository.save(post);
//        return toPostResponse(saved, Collections.emptyList());
//    }

    @Override
    @Transactional(readOnly = true)
    public SocialPostResponse getPost(Long postId) {
        SocialPost post = getActivePost(postId);
        List<SocialCommentResponse> recent = commentRepository.findTop5ByPostAndDeletedAtIsNullOrderByCreatedAtDesc(post)
                .stream()
                .map(this::toCommentResponse)
                .toList();
        return toPostResponse(post, recent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocialPostResponse> getFeed(Long authorId, String tag, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        String normalizedTag = (tag == null || tag.isBlank()) ? null : tag.trim();
        return postRepository.findFeed(authorId, normalizedTag, from, to, pageable)
                .map(post -> toPostResponse(post, Collections.emptyList()));
    }

    @Override
    @Transactional
    public SocialPostResponse updatePost(Long postId, Long actorEmployeeId, boolean actorIsHr, SocialPostUpdateRequest request) {
        SocialPost post = getActivePost(postId);

        if (!actorIsHr && !Objects.equals(post.getAuthor().getId(), actorEmployeeId)) {
            throw new BusinessException("You can edit only your own post");
        }

        if (request.getTitle() != null) {
            String title = request.getTitle().trim();
            if (title.isBlank()) {
                throw new BusinessException("Title cannot be blank");
            }
            post.setTitle(title);
        }

        if (request.getDescription() != null) {
            String description = request.getDescription().trim();
            if (description.isBlank()) {
                throw new BusinessException("Description cannot be blank");
            }
            post.setDescription(description);
        }

        if (request.getVisibility() != null) {
            post.setVisibility(request.getVisibility());
        }

        if (request.getTags() != null) {
            post.setTags(resolveTags(request.getTags()));
        }

        return toPostResponse(postRepository.save(post), Collections.emptyList());
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long actorEmployeeId, boolean actorIsHr, String reason) {
        SocialPost post = getActivePost(postId);
        boolean isOwner = Objects.equals(post.getAuthor().getId(), actorEmployeeId);

        if (!isOwner && !actorIsHr) {
            throw new BusinessException("You can delete only your own post");
        }

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);

        if (actorIsHr && !isOwner) {
            sendHrDeletionEmail(
                    post.getAuthor(),
                    "Your social post was removed by HR",
                    post.getTitle(),
                    reason
            );
        }
    }

    @Override
    @Transactional
    public SocialLikeResponse likePost(Long postId, Long actorEmployeeId) {
        SocialPost post = getActivePost(postId);
        Employee employee = getEmployee(actorEmployeeId);

        if (likeRepository.findByPostAndEmployee(post, employee).isEmpty()) {
            SocialPostLike like = new SocialPostLike();
            like.setPost(post);
            like.setEmployee(employee);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        }

        return new SocialLikeResponse(post.getId(), post.getLikeCount(), true);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long actorEmployeeId) {
        SocialPost post = getActivePost(postId);
        Employee employee = getEmployee(actorEmployeeId);

        likeRepository.findByPostAndEmployee(post, employee).ifPresent(like -> {
            likeRepository.delete(like);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
        });
    }

    @Override
    public SocialPostResponse createPost(Long actorEmployeeId, SocialPostCreateRequest request, List<MultipartFile> images) {
        Employee author = getEmployee(actorEmployeeId);

        SocialPost post = new SocialPost();
        post.setAuthor(author);
        post.setTitle(request.getTitle().trim());
        post.setDescription(request.getDescription().trim());
        post.setVisibility(request.getVisibility() == null ? SocialVisibility.ALL : request.getVisibility());
        post.setTags(resolveTags(request.getTags()));

        SocialPost saved = postRepository.save(post);
        attachImages(saved, images);

        return toPostResponse(saved, Collections.emptyList());
    }

    @Override
    public SocialPostImageContent getPostImageContent(Long imageId) {
        SocialPostImage image = imageRepository.findByIdAndPostDeletedAtIsNull(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Post image not found: " + imageId));
        try {
            return new SocialPostImageContent(
                    image.getFileName(),
                    image.getFileType(),
                    fileStorageService.read(image.getFilePath())
            );
        } catch (IOException ex) {
            throw new BusinessException("Unable to load post image");
        }
    }

    private SocialPost getActivePost(Long postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    private Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    private Set<SocialTag> resolveTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }

        Map<String, String> normalized = new LinkedHashMap<>();
        for (String value : tags) {
            if (value == null) {
                continue;
            }
            String trimmed = value.trim();
            if (!trimmed.isBlank()) {
                normalized.putIfAbsent(trimmed.toLowerCase(Locale.ROOT), trimmed);
            }
        }

        Set<SocialTag> resolved = new HashSet<>();
        for (String tagName : normalized.values()) {
            SocialTag tag = tagRepository.findByNameIgnoreCase(tagName)
                    .orElseGet(() -> {
                        SocialTag newTag = new SocialTag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            resolved.add(tag);
        }
        return resolved;
    }

    private List<SocialPostImageResponse> toImageResponses(SocialPost post) {
        if (post.getImages() == null || post.getImages().isEmpty()) {
            return Collections.emptyList();
        }
        return post.getImages().stream()
                .sorted(Comparator.comparing(SocialPostImage::getId))
                .map(image -> SocialPostImageResponse.builder()
                        .id(image.getId())
                        .fileName(image.getFileName())
                        .fileType(image.getFileType())
                        .fileSize(image.getFileSize())
                        .url("/social/posts/images/" + image.getId())
                        .build())
                .toList();
    }

    private SocialPostResponse toPostResponse(SocialPost post, List<SocialCommentResponse> recentComments) {
        return SocialPostResponse.builder()
                .id(post.getId())
                .author(toActor(post.getAuthor()))
                .title(post.getTitle())
                .description(post.getDescription())
                .tags(post.getTags().stream().map(SocialTag::getName).sorted(String.CASE_INSENSITIVE_ORDER).toList())
                .visibility(post.getVisibility())
                .systemGenerated(post.isSystemGenerated())
                .systemPostType(post.getSystemPostType())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .recentComments(recentComments)
                .images(toImageResponses(post))
                .build();
    }

    private SocialCommentResponse toCommentResponse(SocialPostComment comment) {
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

    private void sendHrDeletionEmail(Employee targetEmployee, String subject, String postTitle, String reason) {
        if (targetEmployee == null || targetEmployee.getUser() == null || targetEmployee.getUser().getEmail() == null) {
            return;
        }
        String finalReason = (reason == null || reason.isBlank()) ? "Inappropriate content" : reason.trim();
        String body = """
                Your content was removed by HR.
                Title: %s
                Reason: %s
                """.formatted(postTitle, finalReason);
        emailService.send(targetEmployee.getUser().getEmail(), subject, body);
    }

    private void attachImages(SocialPost post, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        List<MultipartFile> validImages = images.stream()
                .filter(Objects::nonNull)
                .filter(file -> !file.isEmpty())
                .toList();

        if (validImages.isEmpty()) {
            return;
        }

        if (validImages.size() > MAX_IMAGES_PER_POST) {
            throw new BusinessException("A post can have at most " + MAX_IMAGES_PER_POST + " images");
        }

        List<SocialPostImage> newImages = new ArrayList<>();
        for (MultipartFile image : validImages) {
            String contentType = image.getContentType() == null ? "" : image.getContentType().trim();
            if (!contentType.startsWith("image/")) {
                throw new BusinessException("Only image files are allowed");
            }
            if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
                throw new BusinessException("Each image must be <= 5 MB");
            }
            try {
                String path = fileStorageService.saveSocialPostImage(post.getId(), image);
                SocialPostImage postImage = new SocialPostImage();
                postImage.setPost(post);
                postImage.setFileName(image.getOriginalFilename() == null ? "image" : image.getOriginalFilename());
                postImage.setFileType(contentType);
                postImage.setFileSize(image.getSize());
                postImage.setFilePath(path);
                newImages.add(postImage);
            } catch (IOException ex) {
                throw new BusinessException("Unable to save post image");
            }
        }

        imageRepository.saveAll(newImages);
        post.getImages().addAll(newImages);
    }
}