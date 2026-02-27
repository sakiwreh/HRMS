package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.EmployeeProfileDto;

import java.util.List;

public interface IEmployeeService {
    List<EmployeeLookupDto> getEmployeeLookup();
    EmployeeProfileDto getProfile(Long empId);
    EmployeeProfileDto updateProfile(Long empId, EmployeeProfileDto dto);
    String uploadProfilePhoto(Long empId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;
}
