package com.capestone.hrms_backend.controller.Job;

import com.capestone.hrms_backend.dto.request.AddCvReveiwerRequestDto;
import com.capestone.hrms_backend.dto.request.JobOpeningRequestDto;
import com.capestone.hrms_backend.dto.request.JobStatusRequestDto;
import com.capestone.hrms_backend.dto.response.JobOpeningResponseDto;
import com.capestone.hrms_backend.dto.response.JobOpeningReviewerResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IJobOpeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobOpeningController {

    private final IJobOpeningService jobOpeningService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobOpeningResponseDto> create(@AuthenticationPrincipal HrmsUserDetails user, @Valid @ModelAttribute JobOpeningRequestDto dto) throws IOException{
        return ResponseEntity.ok(jobOpeningService.create(user.getEmpId(),dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOpeningResponseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(jobOpeningService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<JobOpeningResponseDto>> getAll(){
        return ResponseEntity.ok(jobOpeningService.getAllOpenings());
    }

    @GetMapping("/all/hr")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<JobOpeningResponseDto>> getAllForHr() {
        return ResponseEntity.ok(jobOpeningService.getAllOpeningsForHr());
    }

    @PreAuthorize("hasRole('HR')")
    @PatchMapping("/{id}/update")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,@Valid @RequestBody JobStatusRequestDto dto){
        jobOpeningService.changeStatus(id,dto.getStatus(), dto.getReason());
        return ResponseEntity.ok("Status updated!");
    }

    @PreAuthorize("hasRole('HR')")
    @PostMapping("/{id}/add-reviewer")
    public ResponseEntity<String> addReviewer(@PathVariable Long id,@Valid @RequestBody AddCvReveiwerRequestDto dto){
        jobOpeningService.addReviewer(id,dto.getEmpIds());
        return ResponseEntity.ok("Reviewer/s added to job opening");
    }

    @PreAuthorize("hasRole('HR')")
    @DeleteMapping("/{id}/remove-reviewer/{empId}")
    public ResponseEntity<String> removeReviewer(@PathVariable Long id, @PathVariable Long empId){
        jobOpeningService.removeReviewer(id,empId);
        return ResponseEntity.ok("Reveiwer removed from job");
    }

    @GetMapping("/{id}/reviewers")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<JobOpeningReviewerResponseDto>> getReviewers(@PathVariable Long id){
        return ResponseEntity.ok(jobOpeningService.getReviewers(id));
    }



}
