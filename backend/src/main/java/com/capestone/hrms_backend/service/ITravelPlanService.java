package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelParticipantResponseDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ITravelPlanService {
    TravelPlanResponseDto create(TravelPlanRequestDto request, Long employeeId);
    List<TravelPlanResponseDto> getAllTravelPlans();
    TravelPlanResponseDto cancel(Long planId);
    TravelPlanResponseDto getTravel(Long travelPlanId,Long empId, String role);
    void addParticipants(Long travelPlanId, List<Long> empIds);
    void removeParticipant(Long travelPlanId, Long empId);
    List<TravelParticipantResponseDto> getParticipants(Long travelPlanId);
    List<TravelPlanResponseDto> getMyTravels(Long empId);
}
