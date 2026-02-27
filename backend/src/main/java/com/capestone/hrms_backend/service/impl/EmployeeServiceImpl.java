package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.EmployeeProfileDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IEmployeeService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

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

    @Override
    public EmployeeProfileDto updateProfile(Long empId, EmployeeProfileDto dto) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + empId));

        emp.setFirstName(dto.getFirstName());
        emp.setMiddleName(dto.getMiddleName());
        emp.setLastName(dto.getLastName());
        emp.setDesignation(dto.getDesignation());
        emp.setDob(dto.getDob());
        emp.setDoj(dto.getDoj());

        employeeRepository.save(emp);

        EmployeeProfileDto out = modelMapper.map(emp, EmployeeProfileDto.class);
        out.setManagerName(dto.getManagerName());
        out.setProfilePath(emp.getProfilePath());
        return out;
    }

    @Override
    public String uploadProfilePhoto(Long empId, MultipartFile file) throws IOException {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + empId));

        String path = fileStorageService.saveProfile(empId, file);
        emp.setProfilePath(path);
        employeeRepository.save(emp);
        return path;
    }

    private String buildFullName(String first, String middle, String last) {
        StringBuilder sb = new StringBuilder();
        if (first != null) sb.append(first);
        if (middle != null && !middle.isBlank()) sb.append(" ").append(middle);
        if (last != null) sb.append(" ").append(last);
        return sb.toString().trim();
    }
}
