package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.UpcomingMatchResponseDto;

import java.util.List;

public interface IDashboardService {
    int findTotalTravelPlansCreatedByMe(Long hrId);
    int pendingExpenseReview();
    int totalActiveJobs();
    List<UpcomingMatchResponseDto> getUpcomingMatches();
}
