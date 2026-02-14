package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.NotificationResponseDto;

import java.util.List;

public interface INotificationService {
    void create(Long empId,String subject, String body);
    List<NotificationResponseDto> getMyNotifications(Long empId);
    void markAsRead(Long notificationId,Long empId);
    long unreadCount(Long empId);
}
