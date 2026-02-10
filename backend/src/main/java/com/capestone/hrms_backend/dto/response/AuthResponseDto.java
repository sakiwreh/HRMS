package com.capestone.hrms_backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class AuthResponseDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;
}
