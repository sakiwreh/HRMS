package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelParticipantResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import com.capestone.hrms_backend.entity.travel.TravelPlanParticipant;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanParticipantRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanRepository;
import com.capestone.hrms_backend.service.INotificationService;
import com.capestone.hrms_backend.service.ITravelPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelPlanServiceImpl implements ITravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final EmployeeRepository employeeRepository;
    private final TravelPlanParticipantRepository participantRepo;
    private final ModelMapper modelMapper;
    private final INotificationService notificationService;

    @Override
    public TravelPlanResponseDto create(TravelPlanRequestDto request, Long employeeId) {
        Employee hr = employeeRepository.findById(employeeId).orElseThrow(()->new ResourceNotFoundException("Employee not found"));

        //Validations DTO part, config mapper
        if(request.getDepartureDate().isBefore(LocalDateTime.now()))
            throw new BusinessException("Travel must start in future");
        if(request.getReturnDate().isBefore(request.getDepartureDate()))
            throw new BusinessException("Return date must be after departure date");

        TravelPlan plan = new TravelPlan();
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setDestination(request.getDestination());
        plan.setDepatureDate(request.getDepartureDate());
        plan.setReturnDate(request.getReturnDate());
        plan.setMaxPerDayAmount(request.getMaxPerDayAmount());
        plan.setCreatedBy(hr);

        travelPlanRepository.save(plan);
        return modelMapper.map(plan,TravelPlanResponseDto.class);
    }

    @Override
    public List<TravelPlanResponseDto> getAllTravelPlans() {
        return travelPlanRepository.findAll().stream()
                .map(plan -> modelMapper.map(plan,TravelPlanResponseDto.class)).toList();
    }

    @Override
    public TravelPlanResponseDto cancel(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId).orElseThrow(()->new ResourceNotFoundException("Travel Not Found"));
        plan.setCancelled(true);
        travelPlanRepository.save(plan);
        return modelMapper.map(plan,TravelPlanResponseDto.class);
    }

    @Override
    public TravelPlanResponseDto getTravel(Long travelPlanId, Long empId, String role) {
        TravelPlan plan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan doesn't exist"));

        boolean isHr = "HR".equalsIgnoreCase(role);
        boolean isParticipant = participantRepo.findByTravelPlanIdAndEmployeeId(travelPlanId, empId).isPresent();
        boolean isCreator = plan.getCreatedBy() != null && plan.getCreatedBy().getId().equals(empId);
        if (!isHr && !isParticipant && !isCreator) {
            throw new BusinessException("You are not authorized to view this travel plan");
        }

        return modelMapper.map(plan, TravelPlanResponseDto.class);
    }


    @Override
    public void addParticipants(Long travelPlanId, List<Long> empIds) {
        //Fetch travel plan
        TravelPlan plan = travelPlanRepository.findById(travelPlanId).orElseThrow(()->new ResourceNotFoundException("Travel plan not found."));

        for(Long id : empIds){
            log.info("Employee id: {}",id);
            if(participantRepo.findByTravelPlanIdAndEmployeeId(travelPlanId,id).isPresent()){
                continue;
            }

            Employee emp = employeeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee not found."));
            log.info("Employee: {} {}",emp.getFirstName(),emp.getLastName());

            TravelPlanParticipant participant = new TravelPlanParticipant();
            //Add Members
            participant.setTravelPlan(plan);
            participant.setEmployee(emp);
            log.info("employee: {}",participant.getEmployee().getId());
            participantRepo.save(participant);

            String subject = "Travel Assigned: " + plan.getTitle();
            String body = String.format(
                    "You have been assigned to travel \"%s\" to %s (Departure: %s, Return: %s).",
                    plan.getTitle(),
                    plan.getDestination(),
                    plan.getDepatureDate(),
                    plan.getReturnDate()
            );
            notificationService.create(emp.getId(), subject, body);
        }
    }

    @Override
    @Transactional
    public void removeParticipant(Long travelPlanId, Long empId) {
        participantRepo.deleteByTravelPlanIdAndEmployeeId(travelPlanId,empId);
    }

    @Override
    public List<TravelParticipantResponseDto> getParticipants(Long travelPlanId) {
        return participantRepo.findByTravelPlanId(travelPlanId)
                .stream()
                .map(tp -> TravelParticipantResponseDto.builder()
                                .id(tp.getEmployee().getId())
                                .name(tp.getEmployee().getFirstName()+" "+tp.getEmployee().getLastName())
                                .email(tp.getEmployee().getUser().getEmail())
                                .build()).toList();
    }

    @Override
    public List<TravelPlanResponseDto> getMyTravels(Long empId) {
        return participantRepo.findByEmployeeId(empId)
                .stream()
                .map(tp -> modelMapper.map(tp.getTravelPlan(), TravelPlanResponseDto.class))
                .toList();
    }
}
