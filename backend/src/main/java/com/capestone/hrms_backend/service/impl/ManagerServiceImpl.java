package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import com.capestone.hrms_backend.dto.response.TeamMemberResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelPlanParticipant;
import com.capestone.hrms_backend.repository.expense.ExpenseRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanParticipantRepository;
import com.capestone.hrms_backend.service.IManagerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements IManagerService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final TravelPlanParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public List<TeamMemberResponseDto> getTeamMembers(Long mgrId) {
        return employeeRepository.findByManagerId(mgrId).stream()
                .map(emp -> {
                    TeamMemberResponseDto dto = modelMapper.map(emp, TeamMemberResponseDto.class);
                    dto.setName(fullName(emp));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<TravelPlanResponseDto> getTeamMemberTravels(Long mgrId) {
        List<Long> teamIds = getTeamMemberIds(mgrId);
        if (teamIds.isEmpty()) return Collections.emptyList();

        return teamIds.stream()
                .flatMap(empId -> participantRepository.findByEmployeeId(empId).stream())
                .map(TravelPlanParticipant::getTravelPlan)
                .distinct()
                .map(plan -> modelMapper.map(plan, TravelPlanResponseDto.class))
                .toList();
    }

    @Override
    public List<ExpenseResponseDto> getTeamExpenses(Long mgrId) {
        List<Long> teamIds = getTeamMemberIds(mgrId);
        if (teamIds.isEmpty()) return Collections.emptyList();

        return expenseRepository.findByEmployeeIdInAndStatusNot(teamIds, ExpenseStatus.DRAFT).stream()
                .map(exp -> {
                    ExpenseResponseDto dto = modelMapper.map(exp,ExpenseResponseDto.class);
                    dto.setEmployeeName(fullName(exp.getEmployee()));
                    return dto;
                })
                .toList();
    }

    private String fullName(Employee e){
        return e.getFirstName()+ (e.getMiddleName()!=null ? e.getMiddleName() : "") +" "+e.getLastName();
    }

    private List<Long> getTeamMemberIds(Long managerId) {
        return employeeRepository.findByManagerId(managerId).stream()
                .map(Employee::getId)
                .toList();
    }
}
