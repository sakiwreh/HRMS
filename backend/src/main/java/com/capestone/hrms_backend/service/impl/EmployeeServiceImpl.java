package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    @Override
    public List<EmployeeLookupDto> getEmployeeLookup() {
        return employeeRepository.findEmployeeLookup();
    }
}
