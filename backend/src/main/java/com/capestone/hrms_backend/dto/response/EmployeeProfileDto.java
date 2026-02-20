package com.capestone.hrms_backend.dto.response;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeProfileDto {

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String designation;
    private String department;
    private String role;
    private String managerName;
    private LocalDate dob;
    private LocalDate doj;
}