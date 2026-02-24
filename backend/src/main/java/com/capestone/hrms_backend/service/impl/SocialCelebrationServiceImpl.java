package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.social.SocialCelebrationRecord;
import com.capestone.hrms_backend.entity.social.SocialCelebrationType;
import com.capestone.hrms_backend.entity.social.SocialPost;
import com.capestone.hrms_backend.entity.social.SocialVisibility;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.social.SocialCelebrationRecordRepository;
import com.capestone.hrms_backend.repository.social.SocialPostRepository;
import com.capestone.hrms_backend.service.ISocialCelebrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialCelebrationServiceImpl implements ISocialCelebrationService {

    private final SocialCelebrationRecordRepository celebrationRecordRepository;
    private final SocialPostRepository socialPostRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void runDaily() {
        LocalDate today = LocalDate.now();

        List<Employee> birthdays = employeeRepository.findEmployeesHavingBirthday(today);
        for (Employee employee : birthdays) {
            createCelebrationPostIfMissing(employee, SocialCelebrationType.BIRTHDAY, today);
        }

        List<Employee> anniversaries = employeeRepository.findEmployeesHavingAnniversary(today);
        for (Employee employee : anniversaries) {
            createCelebrationPostIfMissing(employee, SocialCelebrationType.WORK_ANNIVERSARY, today);
        }
    }

    private void createCelebrationPostIfMissing(Employee employee, SocialCelebrationType type, LocalDate date) {
        if (celebrationRecordRepository.existsByEmployeeIdAndCelebrationTypeAndCelebrationDate(employee.getId(), type, date)) {
            return;
        }

        SocialPost post = new SocialPost();
        post.setAuthor(employee);
        post.setVisibility(SocialVisibility.ALL);
        post.setSystemGenerated(true);
        post.setSystemPostType(type);

        if (type == SocialCelebrationType.BIRTHDAY) {
            post.setTitle("Today is " + safeName(employee) + "'s birthday");
            post.setDescription("Join in and wish " + safeName(employee) + " a wonderful year ahead.");
        } else {
            if (employee.getDoj() == null) {
                return;
            }
            int years = Period.between(employee.getDoj(), date).getYears();
            if (years <= 0) {
                return;
            }
            post.setTitle(safeName(employee) + " completes " + years + " year(s) at the organization");
            post.setDescription("Congratulations to " + safeName(employee) + " on the work anniversary.");
        }

        SocialPost savedPost = socialPostRepository.save(post);

        SocialCelebrationRecord record = new SocialCelebrationRecord();
        record.setEmployee(employee);
        record.setCelebrationType(type);
        record.setCelebrationDate(date);
        record.setPost(savedPost);
        celebrationRecordRepository.save(record);
    }

    private String safeName(Employee employee) {
        String first = employee.getFirstName() == null ? "" : employee.getFirstName().trim();
        String last = employee.getLastName() == null ? "" : employee.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isBlank() ? "Employee " + employee.getId() : full;
    }
}