package com.capestone.hrms_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TravelParticipantResponseDto {
    private Long id;
    private String name;
    private String email;
}
