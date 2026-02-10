package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.TravelPlanRequestDto;
import com.capestone.hrms_backend.dto.response.TravelPlanResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ITravelPlanService {
    TravelPlanResponseDto create(TravelPlanRequestDto request, Long employeeId);
    List<TravelPlanResponseDto> getAllTravelPlans();
    TravelPlanResponseDto cancel(Long planId, Long employeeId);

}
