package com.capestone.hrms_backend.controller.Travel;

import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.security.HrmsUserDetailsService;
import com.capestone.hrms_backend.service.ITravelPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-plans")
@RequiredArgsConstructor
public class TravelPlanController {
    private final ITravelPlanService travelPlanService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<TravelPlanResponseDto> create(@Valid @RequestBody TravelPlanRequestDto travelPlanRequestDto, Authentication auth){
        HrmsUserDetails user = (HrmsUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(travelPlanService.create(travelPlanRequestDto,user.getEmployeeId()));
    }

    @GetMapping
    public ResponseEntity<List<TravelPlanResponseDto>> list(){
        return ResponseEntity.ok(travelPlanService.getAllTravelPlans());
    }

    @PreAuthorize("hasRole('HR')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TravelPlanResponseDto> cancel(@Valid @PathVariable Long id,Authentication auth){
        HrmsUserDetails user = (HrmsUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(travelPlanService.cancel(id,user.getEmployeeId()));
    }
}
