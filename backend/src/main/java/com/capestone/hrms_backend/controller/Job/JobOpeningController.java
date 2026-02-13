package com.capestone.hrms_backend.controller.Job;

import com.capestone.hrms_backend.dto.request.JobOpeningRequestDto;
import com.capestone.hrms_backend.dto.request.JobStatusRequestDto;
import com.capestone.hrms_backend.dto.response.JobOpeningResponseDto;
import com.capestone.hrms_backend.entity.job.JobStatus;
import com.capestone.hrms_backend.service.IJobOpeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<JobOpeningResponseDto> create(@Valid @ModelAttribute JobOpeningRequestDto dto) throws IOException{
        return ResponseEntity.ok(jobOpeningService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOpeningResponseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(jobOpeningService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<JobOpeningResponseDto>> getAll(){
        return ResponseEntity.ok(jobOpeningService.getAllOpenings());
    }

    @PreAuthorize("hasRole('HR')")
    @PatchMapping("/{id}/update")
    public ResponseEntity<String> updateStatus(@PathVariable Long id,@Valid @RequestBody JobStatusRequestDto dto){
        jobOpeningService.changeStatus(id,dto.getStatus(), dto.getReason());
        return ResponseEntity.ok("Status updated!");
    }
}
