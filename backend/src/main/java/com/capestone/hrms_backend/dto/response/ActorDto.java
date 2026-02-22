package com.capestone.hrms_backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorDto {
    private Long id;
    private String fullName;
    private String email;

}
