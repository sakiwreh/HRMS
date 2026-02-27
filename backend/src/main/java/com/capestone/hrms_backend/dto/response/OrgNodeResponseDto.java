package com.capestone.hrms_backend.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgNodeResponseDto {
    private EmployeeNodeDto selected; //Selected employee
    private List<EmployeeNodeDto> chain; //Top to bottom
    private List<EmployeeNodeDto> reports; //direct level reports

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeNodeDto {
        private Long id;
        private String name;
        private String designation;
        private String department;
        private String profilePath;
    }
}