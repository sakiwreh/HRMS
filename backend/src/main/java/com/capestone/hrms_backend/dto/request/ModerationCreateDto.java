package com.capestone.hrms_backend.dto.request;

import com.capestone.hrms_backend.entity.community.ModerationActionType;
import com.capestone.hrms_backend.entity.community.ModerationTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModerationCreateDto {

    @NotNull
    private ModerationTarget targetType; // POST or COMMENT

    @NotNull
    private Long targetId;

    @NotNull
    private ModerationActionType action; // DELETE, RESTORE, EDIT_REDACT

    @NotBlank
    private String reason;

}
