package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import com.capestone.hrms_backend.dto.response.TeamMemberResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;

import java.util.List;

public interface IManagerService {
    List<TeamMemberResponseDto> getTeamMembers(Long mgrId);
    List<TravelPlanResponseDto> getTeamMemberTravels(Long mgrId);
    List<ExpenseResponseDto> getTeamExpenses(Long mgrId);
}
