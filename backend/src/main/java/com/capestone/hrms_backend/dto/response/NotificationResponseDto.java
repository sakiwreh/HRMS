package com.capestone.hrms_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDto {
    private Long id;
    private String subject;
    private String body;
    @JsonProperty("isRead")
    private Boolean read;
    private LocalDateTime createdAt;
}
