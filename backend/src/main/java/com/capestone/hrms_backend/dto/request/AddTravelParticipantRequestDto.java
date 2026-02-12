package com.capestone.hrms_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddTravelParticipantRequestDto {

    @NotEmpty(message = "Employee list cannot be empty")
    private List<Long> employeeIds;
}
