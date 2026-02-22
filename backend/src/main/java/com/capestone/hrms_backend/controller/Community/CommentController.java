package com.capestone.hrms_backend.controller.Community;

import com.capestone.hrms_backend.dto.request.CommentCreateDto;
import com.capestone.hrms_backend.dto.request.CommentUpdateDto;
import com.capestone.hrms_backend.dto.response.CommentResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.ICommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    // --- List comments under a post ------------------------------------------
    @GetMapping("/achievements/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> list(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.listComments(postId, pageable));
    }

    // --- Add a comment --------------------------------------------------------
    @PostMapping("/achievements/{postId}/comments")
    public ResponseEntity<CommentResponseDto> add(
            @PathVariable Long postId,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody CommentCreateDto request
    ) {
        return ResponseEntity.ok(commentService.addComment(postId, user.getEmployeeId(), request));
    }

    // --- Update a comment (author or HR) -------------------------------------
    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody CommentUpdateDto request
    ) {
        boolean actorIsHr = isHr(user);
        return ResponseEntity.ok(commentService.updateComment(id, user.getEmployeeId(), actorIsHr, request));
    }

    // --- Delete own comment ---------------------------------------------------
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteOwn(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        commentService.softDeleteOwnComment(id, user.getEmployeeId());
        return ResponseEntity.noContent().build();
    }

    // --- HR moderation: delete a comment with reason -------------------------
    @PostMapping("/comments/{id}/moderate/delete")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> hrDelete(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody ModerateDeleteRequest request
    ) {
        commentService.softDeleteByHr(id, user.getEmployeeId(), request.getReason());
        return ResponseEntity.noContent().build();
    }

    private boolean isHr(HrmsUserDetails user) {
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_HR".equals(a.getAuthority()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ModerateDeleteRequest {
        @NotBlank
        private String reason;
    }

}
