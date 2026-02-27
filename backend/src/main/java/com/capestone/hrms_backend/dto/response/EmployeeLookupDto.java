package com.capestone.hrms_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeLookupDto {
    private Long id;
    private String name;
    private String email;
    private String designation;
    private String department;
    private String profilePath;
}
