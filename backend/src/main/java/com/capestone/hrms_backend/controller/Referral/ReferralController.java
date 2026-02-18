package com.capestone.hrms_backend.controller.Referral;

import com.capestone.hrms_backend.dto.request.JobReferralRequestDto;
import com.capestone.hrms_backend.dto.request.JobshareRequestDto;
import com.capestone.hrms_backend.dto.response.JobReferralResponseDto;
import com.capestone.hrms_backend.entity.job.ReferralStatus;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IJobReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReferralController {
    private final IJobReferralService jobReferralService;

    @PostMapping(value = "/job/{id}/share",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> share(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user, @RequestBody JobshareRequestDto dto) {
        jobReferralService.shareJob(id, user.getEmpId(), dto);
        return ResponseEntity.ok("Job shared successfully");
    }

    @PostMapping("/job/{id}/reffer")
    public ResponseEntity<String> reffer(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user, @ModelAttribute JobReferralRequestDto dto) throws IOException {
        jobReferralService.referCandidate(id, user.getEmpId(), dto);
        return ResponseEntity.ok("Candidate referred for job.");
    }

    @GetMapping("/job/referrals/me")
    public ResponseEntity<List<JobReferralResponseDto>> getMyReferrals(@AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(jobReferralService.getMyReferrals(user.getEmpId()));
    }

    @PatchMapping("/job/reffer/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody ReferralStatus status) {
        jobReferralService.updateReferralStatus(id, status);
        return ResponseEntity.ok("Status updated!");
    }
}
