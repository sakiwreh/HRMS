package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeamMemberResponseDto {
    private Long id;
    private String name;
    private String email;
    private String designation;
    private String department;
    private LocalDate dob;
    private LocalDate doj;
    private String profilePath;
}
