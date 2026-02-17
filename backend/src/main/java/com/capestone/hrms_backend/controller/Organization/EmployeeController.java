package com.capestone.hrms_backend.controller.Organization;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final IEmployeeService employeeService;

    @GetMapping("/lookup")
    public ResponseEntity<List<EmployeeLookupDto>> getEmployeeLookup(){
        return ResponseEntity.ok(employeeService.getEmployeeLookup());
    }
}
