package com.capestone.hrms_backend.controller.Community;

import com.capestone.hrms_backend.dto.request.PostCreateDto;
import com.capestone.hrms_backend.dto.request.PostUpdateDto;
import com.capestone.hrms_backend.dto.response.AttachmentResponseDto;
import com.capestone.hrms_backend.dto.response.LikeResponseDto;
import com.capestone.hrms_backend.dto.response.PostResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IAchievementPostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementPostController {
    private final IAchievementPostService achievementPostService;

    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody PostCreateDto request
    ) {
        PostResponseDto created = achievementPostService.createPost(user.getEmployeeId(), request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // --- Get a single post ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getOne(@PathVariable Long id) {
        return achievementPostService.getPost(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Feed with filters ----------------------------------------------------
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> feed(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false, name = "tag") String tagName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(achievementPostService.listFeed(authorId, tagName, from, to, pageable));
    }

    // --- Update a post (author or HR) ----------------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody PostUpdateDto request
    ) {
        boolean actorIsHr = isHr(user);
        PostResponseDto updated = achievementPostService.updatePost(id, user.getEmployeeId(), actorIsHr, request);
        return ResponseEntity.ok(updated);
    }

    // --- Delete own post (soft delete) ---------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwn(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        achievementPostService.softDeleteOwnPost(id, user.getEmployeeId());
        return ResponseEntity.noContent().build();
    }

    // --- HR moderation: delete a post with reason ----------------------------
    @PostMapping("/{id}/moderate/delete")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> hrDelete(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user,
            @Valid @RequestBody ModerateDeleteRequest request
    ) {
        achievementPostService.softDeleteByHr(id, user.getEmployeeId(), request.getReason());
        return ResponseEntity.noContent().build();
    }

    // --- Like / Unlike --------------------------------------------------------
    @PostMapping("/{id}/likes")
    public ResponseEntity<LikeResponseDto> like(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        return ResponseEntity.ok(achievementPostService.like(id, user.getEmployeeId()));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlike(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user
    ) {
        achievementPostService.unlike(id, user.getEmployeeId());
        return ResponseEntity.noContent().build();
    }

    // --- Add attachment (multipart) ------------------------------------------
    @PostMapping(path = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> addAttachment(
            @PathVariable Long id,
            @AuthenticationPrincipal HrmsUserDetails user,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        AttachmentResponseDto dto = achievementPostService.addAttachment(id, user.getEmployeeId(), file);
        return ResponseEntity.ok(dto);
    }

    // --- Helper: role check ---------------------------------------------------
    private boolean isHr(HrmsUserDetails user) {
        return user != null && user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_HR".equals(a.getAuthority()));
    }

    // --- Local request DTO for HR moderation ---------------------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ModerateDeleteRequest {
        @NotBlank
        private String reason;
    }
}
