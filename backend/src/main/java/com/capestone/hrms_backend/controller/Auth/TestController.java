package com.capestone.hrms_backend.controller.Auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/authenticated")
    public ResponseEntity<String> testAll(){
        return new ResponseEntity<>("Authenticated", HttpStatus.OK);
    }


    @PreAuthorize("hasRole('HR')")
    @GetMapping("/hr")
    public String checkHr(){
        return "HR Dashboard";
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String checkEmployee(){
        return "Employee Dashboard";
    }
}
