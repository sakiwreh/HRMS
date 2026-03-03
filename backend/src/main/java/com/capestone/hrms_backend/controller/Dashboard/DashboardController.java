package com.capestone.hrms_backend.controller.Dashboard;

import com.capestone.hrms_backend.dto.response.UpcomingMatchResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/travel-count")
    public ResponseEntity<Integer> findTravelCount(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(dashboardService.findTotalTravelPlansCreatedByMe(user.getEmpId()));
    }

    @GetMapping("/expense-count")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Integer> findPendingTotal(){
        return ResponseEntity.ok(dashboardService.pendingExpenseReview());
    }

    @GetMapping("/active-job-count")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Integer> findJobCount(){
        return ResponseEntity.ok(dashboardService.totalActiveJobs());
    }

    @GetMapping("/upcoming-matches")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<UpcomingMatchResponseDto>> getUpcomingMatches(){
        return ResponseEntity.ok(dashboardService.getUpcomingMatches());
    }
}
