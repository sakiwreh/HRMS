package com.capestone.hrms_backend.controller.Social;

import com.capestone.hrms_backend.dto.social.request.SocialPostCreateRequest;
import com.capestone.hrms_backend.dto.social.request.SocialPostUpdateRequest;
import com.capestone.hrms_backend.dto.social.response.SocialLikeResponse;
import com.capestone.hrms_backend.dto.social.response.SocialPostResponse;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.ISocialPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/social/posts")
@RequiredArgsConstructor
public class SocialPostController {

    private final ISocialPostService socialPostService;

    @PostMapping
    public ResponseEntity<SocialPostResponse> createPost(
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody SocialPostCreateRequest request
    ) {
        SocialPostResponse created = socialPostService.createPost(user.getEmployeeId(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

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
}