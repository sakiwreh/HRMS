package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;

import java.util.List;

public interface IEmployeeService {
    List<EmployeeLookupDto> getEmployeeLookup();
}
