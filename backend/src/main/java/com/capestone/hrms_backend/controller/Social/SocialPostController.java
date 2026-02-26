package com.capestone.hrms_backend.controller.Social;

import com.capestone.hrms_backend.dto.social.request.SocialPostCreateRequest;
import com.capestone.hrms_backend.dto.social.request.SocialPostImageContent;
import com.capestone.hrms_backend.dto.social.request.SocialPostUpdateRequest;
import com.capestone.hrms_backend.dto.social.response.SocialLikeResponse;
import com.capestone.hrms_backend.dto.social.response.SocialPostResponse;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.ISocialPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/social/posts")
@RequiredArgsConstructor
public class SocialPostController {

    private final ISocialPostService socialPostService;

//    @PostMapping
//    public ResponseEntity<SocialPostResponse> createPost(
//            @AuthenticationPrincipal HrmsUserDetails user,
//            @Valid @RequestBody SocialPostCreateRequest request
//    ) {
//        log.info("First method ran");
//        SocialPostResponse created = socialPostService.createPost(user.getEmployeeId(), request);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(created.getId())
//                .toUri();
//        return ResponseEntity.created(location).body(created);
//    }

    @GetMapping
    public ResponseEntity<Page<SocialPostResponse>> getFeed(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(socialPostService.getFeed(authorId, tag, from, to, pageable));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<SocialPostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(socialPostService.getPost(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<SocialPostResponse> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody SocialPostUpdateRequest request
    ) {
        SocialPostResponse updated = socialPostService.updatePost(postId, user.getEmployeeId(), isHr(user), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @RequestParam(required = false) String reason
    ) {
        socialPostService.deletePost(postId, user.getEmployeeId(), isHr(user), reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<SocialLikeResponse> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        return ResponseEntity.ok(socialPostService.likePost(postId, user.getEmployeeId()));
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        socialPostService.unlikePost(postId, user.getEmployeeId());
        return ResponseEntity.noContent().build();
    }

    private boolean isHr(HrmsUserDetails user) {
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_HR".equals(a.getAuthority()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SocialPostResponse> createPostJson(@AuthenticationPrincipal HrmsUserDetails user, @Valid @RequestBody SocialPostCreateRequest request) {
        log.info("Second method ran");
        SocialPostResponse created = socialPostService.createPost(user.getEmployeeId(), request, List.of());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SocialPostResponse> createPostMultipart(@AuthenticationPrincipal HrmsUserDetails user, @Valid @RequestPart("payload") SocialPostCreateRequest request, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        log.info("Multiple images method called");
        SocialPostResponse created = socialPostService.createPost(user.getEmployeeId(), request, images);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<ByteArrayResource> getPostImage(@PathVariable Long imageId) {
        SocialPostImageContent image = socialPostService.getPostImageContent(imageId);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(image.getFileType());
        } catch (Exception ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(new ByteArrayResource(image.getContent()));
    }
}