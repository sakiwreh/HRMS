package com.capestone.hrms_backend.controller.Organization;

import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import com.capestone.hrms_backend.dto.response.TeamMemberResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {
    private final IManagerService managerService;

    @GetMapping("/team")
    public ResponseEntity<List<TeamMemberResponseDto>> getTeamMembers(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(managerService.getTeamMembers(user.getEmpId()));
    }

    @GetMapping("/team/travels")
    public ResponseEntity<List<TravelPlanResponseDto>> getTeamTravels(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(managerService.getTeamMemberTravels(user.getEmpId()));
    }

    @GetMapping("/team/expenses")
    public ResponseEntity<List<ExpenseResponseDto>> getTeamExpenses(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(managerService.getTeamExpenses(user.getEmpId()));
    }
}
