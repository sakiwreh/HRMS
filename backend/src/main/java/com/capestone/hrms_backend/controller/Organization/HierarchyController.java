package com.capestone.hrms_backend.controller.Organization;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgNodeResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class HierarchyController {
    private final IHierarchyService hierarchyService;

    //Employees reporting to own
    @GetMapping("/team")
    public ResponseEntity<List<EmployeeShorterResponseDto>> getTeam(@AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(hierarchyService.getMyDirectTeam(user.getEmpId()));
    }

    //Generate chart with id
    @GetMapping("/org-chart/{empId}")
    public ResponseEntity<OrgNodeResponseDto> orgChart(@PathVariable Long empId) {
        return ResponseEntity.ok(hierarchyService.getOrgChartView(empId));
    }

    //Assign manager by HR
    @PreAuthorize("hasRole('HR')")
    @PatchMapping("/{empId}/manager/{mgrId}")
    public ResponseEntity<String> allocateManager(@PathVariable Long empId, @PathVariable Long mgrId) {
        hierarchyService.allocateManager(empId, mgrId);
        return ResponseEntity.ok("Manager assigned!");
    }
}