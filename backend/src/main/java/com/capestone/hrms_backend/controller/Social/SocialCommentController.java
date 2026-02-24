package com.capestone.hrms_backend.controller.Social;

import com.capestone.hrms_backend.dto.social.request.SocialCommentRequest;
import com.capestone.hrms_backend.dto.social.response.SocialCommentResponse;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.ISocialCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialCommentController {

    private final ISocialCommentService socialCommentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<SocialCommentResponse>> listComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(socialCommentService.listComments(postId, pageable));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<SocialCommentResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody SocialCommentRequest request
    ) {
        return ResponseEntity.ok(socialCommentService.addComment(postId, user.getEmployeeId(), request));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<SocialCommentResponse> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody SocialCommentRequest request
    ) {
        return ResponseEntity.ok(socialCommentService.updateComment(commentId, user.getEmployeeId(), isHr(user), request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @RequestParam(required = false) String reason
    ) {
        socialCommentService.deleteComment(commentId, user.getEmployeeId(), isHr(user), reason);
        return ResponseEntity.noContent().build();
    }

    private boolean isHr(HrmsUserDetails user) {
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_HR".equals(a.getAuthority()));
    }
}