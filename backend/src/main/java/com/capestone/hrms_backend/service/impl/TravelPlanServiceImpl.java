package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanRepository;
import com.capestone.hrms_backend.service.ITravelPlanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelPlanServiceImpl implements ITravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public TravelPlanResponseDto create(TravelPlanRequestDto request, Long employeeId) {
        Employee hr = employeeRepository.findById(employeeId).orElseThrow(()->new ResourceNotFoundException("Employee not found"));

        //Validations
        if(!hr.getRole().getName().equals("HR"))
            throw new BusinessException("Only HR can create travel plans");
        if(request.getDepartureDate().isBefore(LocalDateTime.now()))
            throw new BusinessException("Travel must start in future");
        if(request.getReturnDate().isBefore(request.getDepartureDate()))
            throw new BusinessException("Return date must be after departure date");

        //Fill up: Substitute by model mapper
        TravelPlan plan = new TravelPlan();
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setDestination(request.getDestination());
        plan.setDepatureDate(request.getDepartureDate());
        plan.setReturnDate(request.getReturnDate());
        plan.setHr(hr);

        travelPlanRepository.save(plan);
        return modelMapper.map(plan,TravelPlanResponseDto.class);
    }

    @Override
    public List<TravelPlanResponseDto> getAllTravelPlans() {
        return travelPlanRepository.findAll().stream()
                .map(plan -> modelMapper.map(plan,TravelPlanResponseDto.class)).toList();
    }

    @Override
    public TravelPlanResponseDto cancel(Long planId, Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId).orElseThrow(()->new BusinessException("Employee not found"));

        if(!emp.getRole().getName().equals("HR"))
            throw new BusinessException("Only HR can cancel travel plans");

        TravelPlan plan = travelPlanRepository.findById(planId).orElseThrow(()->new BusinessException("Travel Plan not found."));
        plan.setCancelled(true);
        travelPlanRepository.save(plan);
        return modelMapper.map(plan,TravelPlanResponseDto.class);
    }
}
