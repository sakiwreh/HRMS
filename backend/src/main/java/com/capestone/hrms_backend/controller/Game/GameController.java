package com.capestone.hrms_backend.controller.Game;

import com.capestone.hrms_backend.dto.request.GameRequestDto;
import com.capestone.hrms_backend.dto.request.GameWaitlistRequestDto;
import com.capestone.hrms_backend.dto.response.GameBookingResponseDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameSlotResponseDto;
import com.capestone.hrms_backend.dto.response.GameWaitlistResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IGameAdminService;
import com.capestone.hrms_backend.service.IGamePlayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController{

    private final IGameAdminService adminService;
    private final IGamePlayService gamePlayService;

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<GameResponseDto> create(@Valid @RequestBody GameRequestDto dto) {
        return ResponseEntity.ok(adminService.createGame(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<GameResponseDto> update(@PathVariable Long id, @Valid @RequestBody GameRequestDto dto) {
        return ResponseEntity.ok(adminService.updateGame(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<GameResponseDto> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleActive(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<GameResponseDto>> getAll() {
        return ResponseEntity.ok(adminService.getAllGames());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameResponseDto>> getActive() {
        return ResponseEntity.ok(adminService.getActiveGames());
    }

    @GetMapping("/{gameId}/slots")
    public ResponseEntity<List<GameSlotResponseDto>> getSlots(@PathVariable Long gameId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(adminService.getSlots(gameId, from, to));
    }

    @PostMapping("/{gameId}/slots/generate")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> generateSlots(@PathVariable Long gameId, @RequestParam LocalDate date) {
        adminService.generateSlotsForDate(gameId, date);
        return ResponseEntity.ok("Slots generated successfully");
    }

    //Interest
    @PostMapping("/{gameId}/interest")
    public ResponseEntity<String> registerInterest(@PathVariable Long gameId, @AuthenticationPrincipal HrmsUserDetails user) {
        gamePlayService.registerInterest(gameId, user.getEmployeeId());
        return ResponseEntity.ok("Interest registered");
    }

    @DeleteMapping("/{gameId}/interest")
    public ResponseEntity<String> removeInterest(@PathVariable Long gameId, @AuthenticationPrincipal HrmsUserDetails user) {
        gamePlayService.removeInterest(gameId, user.getEmployeeId());
        return ResponseEntity.ok("Interest removed");
    }

    @GetMapping("/interests/me")
    public ResponseEntity<List<GameResponseDto>> getMyInterests(@AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(gamePlayService.getMyInterests(user.getEmployeeId()));
    }

    //Waitlists
    @PostMapping("/waitlist")
    public ResponseEntity<GameWaitlistResponseDto> submitRequest(@Valid @RequestBody GameWaitlistRequestDto dto, @AuthenticationPrincipal HrmsUserDetails user) {
        log.info("Request and dto incoming: {}",dto);
        return ResponseEntity.ok(gamePlayService.submitRequest(dto, user.getEmployeeId()));
    }

    @PatchMapping("/waitlist/{id}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user) {
        gamePlayService.cancelRequest(id, user.getEmployeeId());
        return ResponseEntity.ok("Request cancelled");
    }

    @GetMapping("/waitlist/me")
    public ResponseEntity<List<GameWaitlistResponseDto>> getMyRequests(@AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(gamePlayService.getMyRequests(user.getEmployeeId()));
    }

    //Booking
    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user) {
        gamePlayService.cancelBooking(id, user.getEmployeeId());
        return ResponseEntity.ok("Booking cancelled");
    }

    @GetMapping("/bookings/me")
    public ResponseEntity<List<GameBookingResponseDto>> getMyBookings(@AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(gamePlayService.getMyBookings(user.getEmployeeId()));
    }

    @GetMapping("/slots/{slotId}/bookings")
    public ResponseEntity<List<GameBookingResponseDto>> getBookingsForSlot(@PathVariable Long slotId) {
        return ResponseEntity.ok(gamePlayService.getBookingsForSlot(slotId));
    }

    @GetMapping("/ExpiredAsComplete")
    public ResponseEntity<String> completeExpiredBookings(){
        gamePlayService.completeExpiredBookings();
        return ResponseEntity.ok("Expired bookings marked as completed");
    }

    @PostMapping("/allocate/{slotId}")
    public ResponseEntity<String> allocateSlot(@PathVariable Long slotId) {
        gamePlayService.allocateSlot(slotId);
        return ResponseEntity.ok("Allocation triggered for slot " + slotId);
    }

    @GetMapping("/FinalizeWaitlist")
    public ResponseEntity<String> finalizeWaitlist(){
        gamePlayService.finalizeExpiredWaitlistEntries();
        return ResponseEntity.ok("Finalized waitlisted entries");
    }
}
