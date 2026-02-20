package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.EmployeeProfileDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeLookupDto> getEmployeeLookup() {
        return employeeRepository.findEmployeeLookup();
    }

    @Override
    public EmployeeProfileDto getProfile(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        EmployeeProfileDto dto = modelMapper.map(emp,EmployeeProfileDto.class);
        String managerName = null;
        if (emp.getManager() != null) {
            Employee mgr = emp.getManager();
            managerName = buildFullName(mgr.getFirstName(), mgr.getMiddleName(), mgr.getLastName());
        }
        dto.setManagerName(managerName);

        return dto;
    }

    private String buildFullName(String first, String middle, String last) {
        StringBuilder sb = new StringBuilder();
        if (first != null) sb.append(first);
        if (middle != null && !middle.isBlank()) sb.append(" ").append(middle);
        if (last != null) sb.append(" ").append(last);
        return sb.toString().trim();
    }
}
