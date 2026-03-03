package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.UpcomingMatchResponseDto;
import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import com.capestone.hrms_backend.entity.game.v2.GameBookingV2;
import com.capestone.hrms_backend.entity.job.JobStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.expense.ExpenseRepository;
import com.capestone.hrms_backend.repository.game.v2.GameBookingV2Repository;
import com.capestone.hrms_backend.repository.job.JobOpeningRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanRepository;
import com.capestone.hrms_backend.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {
    private final TravelPlanRepository travelPlanRepository;
    private final ExpenseRepository expenseRepository;
    private final JobOpeningRepository jobRepository;
    private final GameBookingV2Repository bookingV2Repository;

    @Override
    public int findTotalTravelPlansCreatedByMe(Long hrId) {
        return travelPlanRepository.findByCreatedByIdOrderByDepatureDateDesc(hrId).size();
    }

    @Override
    public int pendingExpenseReview() {
        return expenseRepository.findByStatus(ExpenseStatus.PENDING).size();
    }

    @Override
    public int totalActiveJobs() {
        return jobRepository.findByStatus(JobStatus.OPEN).size();
    }

    @Override
    public List<UpcomingMatchResponseDto> getUpcomingMatches() {
        log.info(""+LocalDateTime.of(LocalDate.now(),LocalTime.MAX));
        return bookingV2Repository.findUpcomingMatchesToday(LocalDateTime.of(LocalDate.now(),LocalTime.MIN), LocalDateTime.of(LocalDate.now(),LocalTime.MAX)).stream().map(this::toBookingDto).toList();
    }

    private UpcomingMatchResponseDto toBookingDto(GameBookingV2 b){
        return UpcomingMatchResponseDto.builder()
                .gameName(b.getGame().getName())
                .slotStart(b.getSlotStart())
                .slotEnd(b.getSlotEnd())
                .bookedByName(fullName(b.getBookedBy()))
                .participantNames(b.getParticipants().stream().map(p-> fullName(p.getEmployee())).toList())
                .build();
    }

    private String fullName(Employee e) {
        return e.getFirstName() + " " + e.getLastName();
    }
}
