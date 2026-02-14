package com.capestone.hrms_backend.controller.System;

import com.capestone.hrms_backend.dto.request.NotificationRequestDto;
import com.capestone.hrms_backend.dto.response.NotificationResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.INotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final INotificationService notificationService;
    private final ModelMapper modelMapper;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(notificationService.getMyNotifications(user.getEmpId()).stream().map(m->modelMapper.map(m,NotificationResponseDto.class)).toList());
    }

    @PostMapping
    public ResponseEntity<String> create(@AuthenticationPrincipal HrmsUserDetails user, @Valid @RequestBody NotificationRequestDto req){
        notificationService.create(user.getEmpId(), req.getSubject(), req.getMessage());
        return ResponseEntity.ok("Notification created.");
    }

    @PatchMapping("/read/{id}")
    public ResponseEntity<String> markRead(@PathVariable Long id,@AuthenticationPrincipal HrmsUserDetails user){
        notificationService.markAsRead(id, user.getEmpId());
        return ResponseEntity.ok("Notification marked as read");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getCount(@AuthenticationPrincipal HrmsUserDetails user){
        return ResponseEntity.ok(notificationService.unreadCount(user.getEmpId()));
    }
}
