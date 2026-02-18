package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgNodeResponseDto;

import java.util.List;

public interface IHierarchyService {
    List<EmployeeShorterResponseDto> getMyDirectTeam(Long empId);
    OrgNodeResponseDto getOrgChartView(Long empId);
    void allocateManager(Long empId, Long managerId);
}
