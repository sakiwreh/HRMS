package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.ExpenseRequestDto;
import com.capestone.hrms_backend.dto.request.ReviewExpenseRequestDto;
import com.capestone.hrms_backend.dto.response.ExpenseCategoryResponseDto;
import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import com.capestone.hrms_backend.entity.expense.Expense;
import com.capestone.hrms_backend.entity.expense.ExpenseCategory;
import com.capestone.hrms_backend.entity.expense.ExpenseStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.expense.ExpenseCategoryRepository;
import com.capestone.hrms_backend.repository.expense.ExpenseRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanParticipantRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanRepository;
import com.capestone.hrms_backend.service.IExpenseService;
import com.capestone.hrms_backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements IExpenseService {

    private final ExpenseRepository repo;
    private final EmployeeRepository employeeRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final TravelPlanParticipantRepository participantRepository;
    private final ModelMapper modelMapper;
    private final INotificationService notificationService;

    @Override
    public ExpenseResponseDto create(Long empId, ExpenseRequestDto requestDto) {

        //Check if Employee exist in travel
        if(participantRepository.findByTravelPlanIdAndEmployeeId(requestDto.getTravelId(),empId).isEmpty()){
            throw new BusinessException("Not part of travel.");
        }

        //Get Employee
        Employee emp = employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee doesn't exist"));

        //Get Travel Plan
        TravelPlan plan = travelPlanRepository.findById(requestDto.getTravelId()).orElseThrow(()->new ResourceNotFoundException("Travel not found"));

        //Get Category
        ExpenseCategory category = expenseCategoryRepository.findById(requestDto.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("Choose valid expense category"));

        //Validation related to dates
        if(requestDto.getExpenseDate().isBefore(plan.getDepatureDate()) || requestDto.getExpenseDate().isAfter(plan.getReturnDate())) throw new BusinessException("Expense must be filed for travel duration only.");

        if(LocalDateTime.now().isAfter(plan.getReturnDate().plusDays(10))) throw new BusinessException("Expense window closed");

        if(LocalDateTime.now().isBefore(plan.getDepatureDate())) throw new BusinessException("You can start filing expense after start date");

        //Validation for macx limit per day
        if (plan.getMaxPerDayAmount() != null) {
            LocalDateTime dayStart = requestDto.getExpenseDate().toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            BigDecimal dailyTotal = repo.sumAmountByEmployeeAndTravelAndDate(empId, plan.getId(), dayStart, dayEnd);
            if (dailyTotal.add(requestDto.getAmount()).compareTo(plan.getMaxPerDayAmount()) > 0) {
                throw new BusinessException(
                        String.format("Daily expense limit exceeded. Max allowed: %s, already claimed: %s, this expense: %s",
                                plan.getMaxPerDayAmount(), dailyTotal, requestDto.getAmount()));
            }
        }

        Expense e = new Expense();
        e.setEmployee(emp);
        e.setTravelPlan(plan);
        e.setCategory(category);
        e.setAmount(requestDto.getAmount());
        e.setDescription(requestDto.getDescription());
        e.setExpenseDate(requestDto.getExpenseDate());
        repo.save(e);

        //Notify HR
        if (plan.getCreatedBy() != null) {
            String subject = "New Expense Submitted: " + e.getDescription();
            String body = String.format(
                    "%s %s submitted an expense of %s for travel \"%s\" — %s.",
                    emp.getFirstName(), emp.getLastName(),
                    e.getAmount(), plan.getTitle(), e.getDescription());
            notificationService.create(plan.getCreatedBy().getId(), subject, body);
        }

        ExpenseResponseDto dto = modelMapper.map(e,ExpenseResponseDto.class);
        dto.setCategory(e.getCategory().getName());
        return dto;
    }

    @Override
    public List<ExpenseResponseDto> myExpenses(Long empId) {
        return repo.findByEmployeeId(empId).stream().map(m->modelMapper.map(m,ExpenseResponseDto.class)).toList();
    }

    @Override
    public List<ExpenseResponseDto> pendingExepnses() {
        return repo.findByStatus(ExpenseStatus.PENDING).stream().map(m->modelMapper.map(m, ExpenseResponseDto.class)).toList();
    }

    @Override
    public ExpenseResponseDto review(Long expenseId, Long hrId, ReviewExpenseRequestDto requestDto) {
        //Check if expense exist
        Expense e = repo.findById(expenseId).orElseThrow(()->new ResourceNotFoundException("Expense doesn't exist"));

        //Review for pending only
        if(e.getStatus()!=ExpenseStatus.PENDING){
            throw new BusinessException("Expense already reviewed");
        }


        Employee hr = employeeRepository.findById(hrId).orElseThrow(()->new ResourceNotFoundException("HR not found."));

        e.setStatus(requestDto.getApproved()?ExpenseStatus.APPROVED:ExpenseStatus.REJECTED);
        log.info("Action: {}",e.getStatus());
        e.setReviewedBy(hr);
        e.setRemarks(requestDto.getRemarks());
        if(e.getStatus().equals(ExpenseStatus.APPROVED)){
            e.setApprovedAt(LocalDateTime.now());
        }
        repo.save(e);

        String status = e.getStatus() == ExpenseStatus.APPROVED ? "Approved" : "Rejected";
        String subject = "Expense " + status + ": " + e.getDescription();
        String body = String.format(
                "Your expense of %s %s for \"%s\" has been %s by HR.",
                e.getAmount(), e.getCurrency() != null ? e.getCurrency() : "",
                e.getDescription(), status.toLowerCase()
        );
        if (e.getRemarks() != null && !e.getRemarks().isBlank()) {
            body += " Remarks: " + e.getRemarks();
        }
        notificationService.create(e.getEmployee().getId(), subject, body);

        return modelMapper.map(e, ExpenseResponseDto.class);
    }

    @Override
    public List<ExpenseCategoryResponseDto> getAllCategories() {
        return expenseCategoryRepository.findAll().stream().map(m->modelMapper.map(m,ExpenseCategoryResponseDto.class)).toList();
    }

    @Override
    public BigDecimal getTotalByTravel(Long travelId) {
        travelPlanRepository.findById(travelId).orElseThrow(()->new ResourceNotFoundException("Travel plan not found"));
        return repo.sumAmountByTravelPlanId(travelId);
    }

    @Override
    public List<ExpenseResponseDto> getFiltered(Long employeeId, ExpenseStatus status, Long travelId,LocalDateTime fromDate, LocalDateTime toDate) {
        return repo.findFiltered(employeeId, status, travelId,fromDate,toDate).stream()
                .map(m -> modelMapper.map(m, ExpenseResponseDto.class)).toList();
    }



}
