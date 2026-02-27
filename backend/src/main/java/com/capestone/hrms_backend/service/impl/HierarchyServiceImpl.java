package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgNodeResponseDto;
import com.capestone.hrms_backend.dto.response.OrgNodeResponseDto.EmployeeNodeDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IHierarchyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HierarchyServiceImpl implements IHierarchyService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeShorterResponseDto> getMyDirectTeam(Long empId) {
        return employeeRepository.findByManagerId(empId).stream()
                .map(this::toShorterDto)
                .toList();
    }

    @Override
    public OrgNodeResponseDto getOrgChartView(Long empId) {
        Employee selected = employeeRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException("Employee not found."));

        //Build chain
        List<EmployeeNodeDto> chain = new ArrayList<>();
        chain.add(toNodeDto(selected));
        Employee current = selected.getManager();
        while (current != null) {
            chain.add(toNodeDto(current));
            current = current.getManager();
        }
        //Reversing to build top to bottom
        Collections.reverse(chain);

        //Immediate reports
        List<EmployeeNodeDto> reports = employeeRepository.findByManagerId(empId).stream()
                .map(this::toNodeDto)
                .toList();

        return new OrgNodeResponseDto(toNodeDto(selected), chain, reports);
    }

    @Override
    public void allocateManager(Long empId, Long managerId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee doesn't exist"));
        Employee mgr = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager doesn't exist"));
        emp.setManager(mgr);
        employeeRepository.save(emp);
    }

    private EmployeeNodeDto toNodeDto(Employee e) {
        return new EmployeeNodeDto(
                e.getId(),
                e.getFirstName() + " " + e.getLastName(),
                e.getDesignation(),
                e.getDepartment().getName(),
                e.getProfilePath()
        );
    }

    private EmployeeShorterResponseDto toShorterDto(Employee e) {
        EmployeeShorterResponseDto dto = new EmployeeShorterResponseDto();
        dto.setId(e.getId());
        dto.setName(e.getFirstName() + " " + e.getLastName());
        dto.setDesignation(e.getDesignation());
        return dto;
    }
}