package com.capestone.hrms_backend.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto {
    private Long id;
    @NotBlank
    private String name;
}
