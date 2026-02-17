package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgChartRespnseDto;
import com.capestone.hrms_backend.entity.organization.Employee;

import java.util.List;

public interface IHierarchyService {
    List<EmployeeShorterResponseDto> getMyDirectTeam(Long empId);
    List<EmployeeShorterResponseDto> getMyFullTeam(Long empId);
    OrgChartRespnseDto getOrgChart(Long empId);
    List<Employee> getOrganization();
    void allocateManager(Long empId, Long managerId);
}
