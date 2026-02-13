package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgChartRespnseDto;

import java.util.List;

public interface IHierarchyService {
    List<EmployeeShorterResponseDto> getMyDirectTeam(Long empId);
    List<EmployeeShorterResponseDto> getMyFullTeam(Long empId);
    OrgChartRespnseDto getOrgChart(Long empId);
    void allocateManager(Long empId, Long managerId);
}
