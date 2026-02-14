package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.NotificationResponseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.system.Notification;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.system.NotificationRepository;
import com.capestone.hrms_backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final EmployeeRepository employeeRepository;
    public final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public void create(Long empId, String subject, String body) {
        Employee emp = employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee doesn't exist"));
        Notification n = new Notification();
        n.setEmployee(emp);
        n.setSubject(subject);
        n.setBody(body);
        notificationRepository.save(n);
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications(Long empId) {
        return notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(empId)
                .stream().map(n->modelMapper.map(n, NotificationResponseDto.class))
                .toList();
    }

    @Override
    public void markAsRead(Long notificationId, Long empId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow(()->new ResourceNotFoundException("Notification not found!"));
        if(!n.getEmployee().getId().equals(empId)) {
            throw new BusinessException("do not have permission!");
        }

            n.setRead(true);
            notificationRepository.save(n);
    }

    @Override
    public long unreadCount(Long empId) {
        return notificationRepository.countByEmployeeIdAndIsReadFalse(empId);
    }
}
