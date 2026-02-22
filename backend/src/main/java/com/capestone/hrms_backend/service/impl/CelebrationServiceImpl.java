package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.entity.community.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.community.AchievementPostRepository;
import com.capestone.hrms_backend.repository.community.CelebrationJobRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.ICelebrationService;
import com.capestone.hrms_backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CelebrationServiceImpl implements ICelebrationService {

    private final CelebrationJobRepository jobRepo;
    private final AchievementPostRepository postRepo;
    private final EmployeeRepository employeeRepo;
    private final INotificationService notificationService;

    @Override
    @Transactional
    public void runDaily() {
        LocalDate today = LocalDate.now();

        // Create or ensure jobs exist for today
        List<Employee> birthdays = employeeRepo.findEmployeesHavingBirthday(today);
        List<Employee> anniversaries = employeeRepo.findEmployeesHavingAnniversary(today);

        birthdays.forEach(e ->
                jobRepo.findByTypeAndEmployeeAndTargetDate(CelebrationType.BIRTHDAY, e, today)
                        .orElseGet(() -> jobRepo.save(CelebrationJob.builder()
                                .type(CelebrationType.BIRTHDAY)
                                .employee(e)
                                .targetDate(today)
                                .build()))
        );

        anniversaries.forEach(e ->
                jobRepo.findByTypeAndEmployeeAndTargetDate(CelebrationType.ANNIVERSARY, e, today)
                        .orElseGet(() -> jobRepo.save(CelebrationJob.builder()
                                .type(CelebrationType.ANNIVERSARY)
                                .employee(e)
                                .targetDate(today)
                                .build()))
        );

        // Process pending jobs
        jobRepo.findByStatusAndTargetDate(CelebrationJobStatus.PENDING, today).forEach(job -> {
            String title;
            String description;

            if (job.getType() == CelebrationType.BIRTHDAY) {
                title = "Happy Birthday " + job.getEmployee().getFirstName() + "!";
                description = "Join us in wishing " + job.getEmployee().getFirstName() + " a wonderful year ahead.";
            } else {
                int years = Period.between(job.getEmployee().getDoj(), today).getYears();
                title = "Work Anniversary";
                description = job.getEmployee().getFirstName() + " completes " + years + " year(s) at our organization today!";
            }

            AchievementPost post = AchievementPost.builder()
                    .author(job.getEmployee()) // or a dedicated system account
                    .title(title)
                    .description(description)
                    .visibility(Visibility.ALL)
                    .systemGenerated(true)
                    .build();

            post = postRepo.save(post);

            job.setPost(post);
            job.setStatus(CelebrationJobStatus.POSTED);

            notificationService.create(job.getEmployee().getId(),
                    "Celebration posted",
                    "Your celebration post was shared on the Achievements feed.");
        });
    }
}
