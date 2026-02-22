package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.GameWaitlistRequestDto;
import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.GameBookingResponseDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameWaitlistResponseDto;

import java.util.List;

public interface    IGamePlayService {

    //Interest
    public void registerInterest(Long gameId, Long employeeId);
    public void removeInterest(Long gameId, Long employeeId);
    public List<GameResponseDto> getMyInterests(Long employeeId);
    List<EmployeeLookupDto> getInterestedEmployees(Long gameId);

    //Request
    public GameWaitlistResponseDto submitRequest(GameWaitlistRequestDto dto, Long requestedById);
    public void cancelRequest(Long waitlistId, Long employeeId);
    public List<GameWaitlistResponseDto> getMyRequests(Long employeeId);

    //Booking
    public void cancelBooking(Long bookingId, Long employeeId);
    public List<GameBookingResponseDto> getMyBookings(Long employeeId);
    public List<GameBookingResponseDto> getBookingsForSlot(Long slotId);
    void allocateSlot(Long slotId);

    //System Updation
    public void completeExpiredBookings();
    public void finalizeExpiredWaitlistEntries();


}