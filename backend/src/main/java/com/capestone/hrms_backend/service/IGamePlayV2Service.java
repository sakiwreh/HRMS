package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.GameBookingV2RequestDto;
import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.GameBookingV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameV2ResponseDto;

import java.util.List;

public interface IGamePlayV2Service {

    // ── Interest ──
    void registerInterest(Long gameId, Long employeeId);
    void removeInterest(Long gameId, Long employeeId);
    List<GameV2ResponseDto> getMyInterests(Long employeeId);
    List<EmployeeLookupDto> getInterestedEmployees(Long gameId);

    // ── Booking requests ──
    GameBookingV2ResponseDto submitRequest(Long gameId, GameBookingV2RequestDto dto, Long requestedById);
    void cancelRequest(Long bookingId, Long employeeId);
    List<GameBookingV2ResponseDto> getMyRequests(Long employeeId);
    List<GameBookingV2ResponseDto> getMyBookings(Long employeeId);

    //Scheduler called methods
    void allocatePendingSlots();
    void completeExpiredBookings();
    void expireStaleRequests();
}