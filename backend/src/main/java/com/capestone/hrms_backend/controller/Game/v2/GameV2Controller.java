package com.capestone.hrms_backend.controller.Game.v2;

import com.capestone.hrms_backend.dto.request.GameBookingV2RequestDto;
import com.capestone.hrms_backend.dto.request.GameV2RequestDto;
import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.GameBookingV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameSlotV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameV2ResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IGameAdminV2Service;
import com.capestone.hrms_backend.service.IGamePlayV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/games/v2")
@RequiredArgsConstructor
public class GameV2Controller {

    private final IGameAdminV2Service adminService;
    private final IGamePlayV2Service playService;

    @PostMapping
    public ResponseEntity<GameV2ResponseDto> createGame(@Valid @RequestBody GameV2RequestDto dto) {
        return ResponseEntity.ok(adminService.createGame(dto));
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<GameV2ResponseDto> updateGame(@PathVariable Long gameId,
                                                        @Valid @RequestBody GameV2RequestDto dto) {
        return ResponseEntity.ok(adminService.updateGame(gameId, dto));
    }

    @PatchMapping("/{gameId}/toggle")
    public ResponseEntity<GameV2ResponseDto> toggleActive(@PathVariable Long gameId) {
        return ResponseEntity.ok(adminService.toggleActive(gameId));
    }

    @GetMapping
    public ResponseEntity<List<GameV2ResponseDto>> getAllGames() {
        return ResponseEntity.ok(adminService.getAllGames());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameV2ResponseDto>> getActiveGames() {
        return ResponseEntity.ok(adminService.getActiveGames());
    }

    @GetMapping("/{gameId}/slots")
    public ResponseEntity<List<GameSlotV2ResponseDto>> getComputedSlots(
            @PathVariable Long gameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminService.getComputedSlots(gameId, date));
    }

    @PostMapping("/{gameId}/interest")
    public ResponseEntity<String> registerInterest(@PathVariable Long gameId,
                                                   @AuthenticationPrincipal HrmsUserDetails user) {
        playService.registerInterest(gameId, user.getEmployeeId());
        return ResponseEntity.ok("Interest registered");
    }

    @DeleteMapping("/{gameId}/interest")
    public ResponseEntity<String> removeInterest(@PathVariable Long gameId,
                                                 @AuthenticationPrincipal HrmsUserDetails user) {
        playService.removeInterest(gameId, user.getEmployeeId());
        return ResponseEntity.ok("Interest removed");
    }

    @GetMapping("/interests/me")
    public ResponseEntity<List<GameV2ResponseDto>> getMyInterests(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(playService.getMyInterests(user.getEmployeeId()));
    }

    @GetMapping("/{gameId}/interested-employees")
    public ResponseEntity<List<EmployeeLookupDto>> getInterestedEmployees(@PathVariable Long gameId) {
        return ResponseEntity.ok(playService.getInterestedEmployees(gameId));
    }

    @PostMapping("/{gameId}/requests")
    public ResponseEntity<GameBookingV2ResponseDto> submitRequest(@PathVariable Long gameId,
                                                                  @Valid @RequestBody GameBookingV2RequestDto dto,
                                                                  @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(playService.submitRequest(gameId, dto, user.getEmployeeId()));
    }

    @PatchMapping("/requests/{bookingId}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long bookingId,
                                                @AuthenticationPrincipal HrmsUserDetails user) {
        playService.cancelRequest(bookingId, user.getEmployeeId());
        return ResponseEntity.ok("Booking cancelled");
    }

    @GetMapping("/requests/me")
    public ResponseEntity<List<GameBookingV2ResponseDto>> getMyRequests(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(playService.getMyRequests(user.getEmployeeId()));
    }

    @GetMapping("/bookings/me")
    public ResponseEntity<List<GameBookingV2ResponseDto>> getMyBookings(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(playService.getMyBookings(user.getEmployeeId()));
    }

    //Testing scheduler
    @PostMapping("/scheduler/allocate")
    public ResponseEntity<String> allocatePending() {
        playService.allocatePendingSlots();
        return ResponseEntity.ok("Allocation completed");
    }

    @PostMapping("/scheduler/complete")
    public ResponseEntity<String> completeExpired() {
        playService.completeExpiredBookings();
        return ResponseEntity.ok("Completion check done");
    }

    @PostMapping("/scheduler/expire")
    public ResponseEntity<String> expireStale() {
        playService.expireStaleRequests();
        return ResponseEntity.ok("Stale requests expired");
    }
}