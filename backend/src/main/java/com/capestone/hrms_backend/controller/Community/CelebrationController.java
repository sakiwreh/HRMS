package com.capestone.hrms_backend.controller.Community;

import com.capestone.hrms_backend.service.ICelebrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/celebrations")
@RequiredArgsConstructor
public class CelebrationController {
    private final ICelebrationService celebrationService;
    @PostMapping("/run-daily")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> runDaily() {
        celebrationService.runDaily();
        return ResponseEntity.noContent().build();
    }
}