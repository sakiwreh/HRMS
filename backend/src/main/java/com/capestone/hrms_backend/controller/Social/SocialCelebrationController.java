package com.capestone.hrms_backend.controller.Social;

import com.capestone.hrms_backend.service.ISocialCelebrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social/celebrations")
@RequiredArgsConstructor
public class SocialCelebrationController {

    private final ISocialCelebrationService socialCelebrationService;

    @PostMapping("/run-daily")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> runDaily() {
        socialCelebrationService.runDaily();
        return ResponseEntity.noContent().build();
    }
}