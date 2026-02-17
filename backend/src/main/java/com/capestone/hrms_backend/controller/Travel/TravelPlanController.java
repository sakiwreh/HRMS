package com.capestone.hrms_backend.controller.Travel;

import com.capestone.hrms_backend.dto.request.AddTravelParticipantRequestDto;
import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelParticipantResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.security.HrmsUserDetailsService;
import com.capestone.hrms_backend.service.ITravelPlanService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-plans")
@RequiredArgsConstructor
public class TravelPlanController {
    private final ITravelPlanService travelPlanService;

    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<TravelPlanResponseDto> create(@Valid @RequestBody TravelPlanRequestDto travelPlanRequestDto, @AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(travelPlanService.create(travelPlanRequestDto,user.getEmployeeId()));
    }

    @GetMapping
    public ResponseEntity<List<TravelPlanResponseDto>> list(){
        return ResponseEntity.ok(travelPlanService.getAllTravelPlans());
    }

    @PreAuthorize("hasRole('HR')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TravelPlanResponseDto> cancel(@Valid @PathVariable Long id){
        return ResponseEntity.ok(travelPlanService.cancel(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<TravelPlanResponseDto> getById(@PathVariable Long id,@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(travelPlanService.getTravel(id, user.getEmpId(), user.getRoleName()));
    }

    @PostMapping("/{id}/add-participants")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> addParticipant(@PathVariable Long id, @Valid @RequestBody AddTravelParticipantRequestDto employees){
        travelPlanService.addParticipants(id,employees.getEmployeeIds());
        return ResponseEntity.ok("Employees added to travel plan");
    }

    @PostMapping("/{id}/remove-participants/{empId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> removeParticipant(@PathVariable Long id,@PathVariable Long empId){
        travelPlanService.removeParticipant(id,empId);
        return ResponseEntity.ok("Employee removed from Travel Plan");
    }

    @GetMapping("/{id}/participants")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<TravelParticipantResponseDto>> getParticipants(@PathVariable Long id){
        return ResponseEntity.ok(travelPlanService.getParticipants(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<TravelPlanResponseDto>> getMyTravels(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(travelPlanService.getMyTravels(user.getEmployeeId()));
    }
}
