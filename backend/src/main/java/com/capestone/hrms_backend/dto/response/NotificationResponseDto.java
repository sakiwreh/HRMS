package com.capestone.hrms_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDto {
    private Long id;
    private String subject;
    private String body;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
