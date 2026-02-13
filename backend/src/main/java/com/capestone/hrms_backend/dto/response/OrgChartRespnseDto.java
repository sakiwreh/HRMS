package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrgChartRespnseDto {
    private Long id;
    private String name;
    private String designation;
    private List<OrgChartRespnseDto> subordinates = new ArrayList<>();
}
