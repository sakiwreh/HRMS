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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

        //Validation: Expense Date
        if(requestDto.getExpenseDate().isBefore(plan.getDepatureDate())) throw new BusinessException("Expense must be filed for travel duration only.");

        if(LocalDateTime.now().isAfter(plan.getReturnDate().plusDays(10))) throw new BusinessException("Expense window closed");

        Expense e = new Expense();
        e.setEmployee(emp);
        e.setTravelPlan(plan);
        e.setCategory(category);
        e.setAmount(requestDto.getAmount());
        e.setDescription(requestDto.getDescription());
        e.setExpenseDate(requestDto.getExpenseDate());
        repo.save(e);

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

        return modelMapper.map(e, ExpenseResponseDto.class);
    }

    @Override
    public List<ExpenseCategoryResponseDto> getAllCategories() {
        return expenseCategoryRepository.findAll().stream().map(m->modelMapper.map(m,ExpenseCategoryResponseDto.class)).toList();
    }
}
