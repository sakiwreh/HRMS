package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.GameWaitlistRequestDto;
import com.capestone.hrms_backend.dto.response.GameBookingResponseDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameWaitlistResponseDto;
import com.capestone.hrms_backend.entity.game.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.game.*;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IGamePlayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePlayServiceImpl implements IGamePlayService {
     public final GameRepository gameRepository;
     public final GameSlotRepository gameSlotRepository;
     private final GameInterestRepository interestRepo;
     private final GameCycleRepository cycleRepo;
     private final GameHistoryRepository historyRepo;
     private final GameWaitlistRepository waitlistRepo;
     private final GameWaitlistParticipantRepository waitlistParticipantRepo;
     private final GameBookingRepository bookingRepo;
     private final EmployeeRepository employeeRepo;

    @Override
    @Transactional
    public void registerInterest(Long gameId, Long employeeId) {
        Game game = findGame(gameId);
        if (!game.isActive()) throw new BusinessException("Game is not active");

        Employee emp = findEmployee(employeeId);
        if (interestRepo.existsByEmployeeIdAndGameId(employeeId, gameId)) throw new BusinessException("Already registered interest");

        GameInterest interest = new GameInterest();
        interest.setEmployee(emp);
        interest.setGame(game);
        interestRepo.save(interest);
    }

    @Override
    @Transactional
    public void removeInterest(Long gameId, Long employeeId) {
        GameInterest interest = interestRepo.findByEmployeeIdAndGameId(employeeId, gameId).orElseThrow(() -> new ResourceNotFoundException("Interest registration not found"));

        //Check if already in waitlist
        boolean hasPending = waitlistRepo.findByRequestedByIdAndStatus(employeeId, WaitlistStatus.WAIT).stream().anyMatch(w -> w.getGame().getId().equals(gameId));
        if (hasPending) throw new BusinessException("Cancel pending requests before removing interest");

        interestRepo.delete(interest);
    }

    @Override
    public List<GameResponseDto> getMyInterests(Long employeeId) {
        return interestRepo.findByEmployeeId(employeeId).stream().map(i -> toGameDto(i.getGame())).toList();
    }

    @Override
    @Transactional
    public GameWaitlistResponseDto submitRequest(GameWaitlistRequestDto dto, Long requestedById) {
        GameSlot slot = findSlot(dto.getSlotId());
        Game game = slot.getGame();

        //Check slot available or not
        validateSlotForRequest(slot);

        //Check interest present or not
        Employee requester = findEmployee(requestedById);
        requireInterest(requestedById, game.getId());

        // Add employee booking slot in participant
        Set<Long> pids = new LinkedHashSet<>(dto.getParticipantIds());
        pids.add(requestedById);

        //Validate all participants
        validateParticipants(pids, game, slot);

        int cycleNum = getOrCreateCycleNumber(game);
        int priority = getPlayCount(requestedById, game.getId(), cycleNum);

        //Create waitlist entry with participants
        GameWaitList wl = new GameWaitList();
        wl.setGame(game);
        wl.setSlot(slot);
        wl.setRequestedBy(requester);
        wl.setAppliedDateTime(LocalDateTime.now());
        wl.setPriorityScore(priority);
        waitlistRepo.save(wl);

        for (Long pid : pids) {
            GameWaitListParticipant wp = new GameWaitListParticipant();
            wp.setWaitlist(wl);
            wp.setEmployee(findEmployee(pid));
            wl.getParticipants().add(wp);
        }
        waitlistRepo.save(wl);

        //Allocating
        allocateSlot(slot.getId());

        //Re-fetch status
        wl = waitlistRepo.findById(wl.getId()).orElseThrow(() -> new ResourceNotFoundException("Waitlist entry not found"));
        return toWaitlistDto(wl);
    }

    @Override
    @Transactional
    public void cancelRequest(Long waitlistId, Long employeeId) {
        GameWaitList wl = waitlistRepo.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!wl.getRequestedBy().getId().equals(employeeId))
            throw new BusinessException("Only the requester can cancel this request");
        if (wl.getStatus() != WaitlistStatus.WAIT)
            throw new BusinessException("Only pending requests can be cancelled");

        wl.setStatus(WaitlistStatus.CANCELLED);
        waitlistRepo.save(wl);
    }

    @Override
    public List<GameWaitlistResponseDto> getMyRequests(Long employeeId) {
        return waitlistRepo.findByRequestedByIdOrderByAppliedDateTimeDesc(employeeId).stream().map(this::toWaitlistDto).toList();
    }

    //Booking
    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long employeeId) {
        GameBooking bk = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!bk.getBookedBy().getId().equals(employeeId))
            throw new BusinessException("Cancellation must be by requestor");
        if (bk.getStatus() != BookingStatus.ACTIVE)
            throw new BusinessException("Booking not active");

        //Check if within configured cancellation time
        int cancMins = bk.getGame().getCancellationBeforeMins();
        if (LocalDateTime.now().isAfter(bk.getSlot().getSlotStart().minusMinutes(cancMins)))
            throw new BusinessException("Cancellation window has passed. Must cancel at least "+ cancMins +" minutes before slot start");

        bk.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(bk);

        //Free slot and trigger allocation
        GameSlot slot = bk.getSlot();
        slot.setBookedCount(Math.max(0, slot.getBookedCount() - bk.getParticipants().size()));
        if (slot.getStatus() == SlotStatus.LOCKED) slot.setStatus(SlotStatus.AVAILABLE);
        gameSlotRepository.save(slot);

        allocateSlot(slot.getId());
    }

    @Override
    public List<GameBookingResponseDto> getMyBookings(Long employeeId) {
        return bookingRepo.findByParticipantEmployeeId(employeeId).stream().map(this::toBookingDto).toList();
    }

    @Override
    public List<GameBookingResponseDto> getBookingsForSlot(Long slotId) {
        findSlot(slotId);
        return bookingRepo.findBySlotId(slotId).stream().map(this::toBookingDto).toList();
    }

    @Override
    @Transactional
    public void completeExpiredBookings() {
        List<GameBooking> expired = bookingRepo.findCompletableBookings(LocalDateTime.now());

        for (GameBooking bk : expired) {
            bk.setStatus(BookingStatus.COMPLETED);
            bookingRepo.save(bk);

            Game game = bk.getGame();
            int cycleNum = getOrCreateCycleNumber(game);
            incrementPlayCounts(bk, game, cycleNum);

            // if all active bookings on this slot are done, mark slot complete
            if (bookingRepo.findBySlotIdAndStatus(bk.getSlot().getId(), BookingStatus.ACTIVE).isEmpty()) {
                bk.getSlot().setStatus(SlotStatus.COMPLETED);
                gameSlotRepository.save(bk.getSlot());
            }
            checkCycleRollover(game);
        }
    }

    @Override
    @Transactional
    public void finalizeExpiredWaitlistEntries() {
        List<GameWaitList> expired = waitlistRepo.findExpiredWaitEntries(LocalDateTime.now());
        for (GameWaitList entry : expired) {
            entry.setStatus(WaitlistStatus.CYCLE_END);
            waitlistRepo.save(entry);
        }
    }

    //Allocatior
    /**
     * Processes the waitlist for a slot in priority order (lowest play-count first,then earliest request, then lowest ID as tiebreaker). Creates bookings for entries that fit within remaining capacity.
     */
    private void allocateSlot(Long slotId) {
        GameSlot slot = findSlot(slotId);
        Game game = slot.getGame();

        if (slot.getStatus() == SlotStatus.COMPLETED || slot.getStatus() == SlotStatus.CANCELLED) return;

        List<GameWaitList> queue = waitlistRepo
                .findBySlotIdAndStatusOrderByPriorityScoreAscAppliedDateTimeAscIdAsc(slotId, WaitlistStatus.WAIT);

        int capacity = game.getMaxPlayersPerSlot();
        int occupied = slot.getBookedCount();

        for (GameWaitList entry : queue) {
            int needed = entry.getParticipants().size();
            if (occupied + needed > capacity) continue;

            // skip if any participant already has an active booking for this game today
            boolean conflict = entry.getParticipants().stream()
                    .anyMatch(p -> bookingRepo.hasActiveBookingForGameOnDate(
                            p.getEmployee().getId(), game.getId(), slot.getSlotDate()));
            if (conflict) continue;

            // convert waitlist entry
            GameBooking bk = new GameBooking();
            bk.setGame(game);
            bk.setSlot(slot);
            bk.setBookedBy(entry.getRequestedBy());
            bk.setBookingDateTime(LocalDateTime.now());
            bookingRepo.save(bk);

            for (GameWaitListParticipant wp : entry.getParticipants()) {
                GameBookingParticipant bp = new GameBookingParticipant();
                bp.setBooking(bk);
                bp.setEmployee(wp.getEmployee());
                bk.getParticipants().add(bp);
            }
            bookingRepo.save(bk);

            entry.setStatus(WaitlistStatus.ALLOCATED);
            entry.setBooking(bk);
            waitlistRepo.save(entry);

            occupied += needed;
            if (occupied >= capacity) break;
        }

        slot.setBookedCount(occupied);
        if (occupied >= capacity) slot.setStatus(SlotStatus.LOCKED);
        gameSlotRepository.save(slot);
    }

    //Cycle management
    /**
     * When ALL interested employees have played at least once in the current cycle,
     * close it and start a new one. This resets everyone's priority back to 0.
     */
    private void checkCycleRollover(Game game) {
        GameCycle active = cycleRepo.findByGameIdAndEndedAtIsNull(game.getId()).orElse(null);
        if (active == null) return;

        long interested = interestRepo.countByGameId(game.getId());
        if (interested == 0) return;

        long played = historyRepo.countByGameIdAndCycleNumberAndPlayCountGreaterThanEqual(
                game.getId(), active.getCycleNumber(), 1);

        if (played >= interested) {
            active.setEndedAt(LocalDateTime.now());
            cycleRepo.save(active);

            GameCycle next = new GameCycle();
            next.setGame(game);
            next.setCycleNumber(active.getCycleNumber() + 1);
            next.setStartedAt(LocalDateTime.now());
            cycleRepo.save(next);

            log.info("Cycle rollover for game {}: {} -> {}", game.getName(),
                    active.getCycleNumber(), next.getCycleNumber());
        }
    }

    /** Returns the active cycle number, creating cycle 1 if none exists. */
    private int getOrCreateCycleNumber(Game game) {
        return cycleRepo.findByGameIdAndEndedAtIsNull(game.getId())
                .map(GameCycle::getCycleNumber)
                .orElseGet(() -> {
                    GameCycle c = new GameCycle();
                    c.setGame(game);
                    c.setCycleNumber(1);
                    c.setStartedAt(LocalDateTime.now());
                    cycleRepo.save(c);
                    return 1;
                });
    }

    private void incrementPlayCounts(GameBooking bk, Game game, int cycleNum) {
        for (GameBookingParticipant bp : bk.getParticipants()) {
            GameHistory h = historyRepo
                    .findByGameIdAndEmployeeIdAndCycleNumber(game.getId(), bp.getEmployee().getId(), cycleNum)
                    .orElseGet(() -> {
                        GameHistory nh = new GameHistory();
                        nh.setGame(game);
                        nh.setEmployee(bp.getEmployee());
                        nh.setCycleNumber(cycleNum);
                        return nh;
                    });
            h.setPlayCount(h.getPlayCount() + 1);
            h.setLastPlayedAt(LocalDateTime.now());
            historyRepo.save(h);
        }
    }

    // ══════════════════ VALIDATION HELPERS ════════════════════

    private void validateSlotForRequest(GameSlot slot) {
        if (slot.getStatus() != SlotStatus.AVAILABLE)
            throw new BusinessException("Slot is not available for booking");
        if (slot.getSlotStart().isBefore(LocalDateTime.now()))
            throw new BusinessException("Cannot request a slot that has already started");
    }

    private void validateParticipants(Set<Long> pids, Game game, GameSlot slot) {
        if (pids.size() > game.getMaxPlayersPerSlot())
            throw new BusinessException("Participant count exceeds max players per slot (" + game.getMaxPlayersPerSlot() + ")");

        for (Long pid : pids) {
            requireInterest(pid, game.getId());
            if (bookingRepo.hasActiveBookingForGameOnDate(pid, game.getId(), slot.getSlotDate()))
                throw new BusinessException("A participant already has an active booking for this game today");
        }

        if (waitlistRepo.existsBySlotIdAndRequestedByIdAndStatus(slot.getId(), pids.iterator().next(), WaitlistStatus.WAIT))
            throw new BusinessException("You already have a pending request for this slot");

        List<Long> taken = waitlistParticipantRepo.findActiveParticipantEmployeeIdsBySlotId(slot.getId());
        for (Long pid : pids) {
            if (taken.contains(pid))
                throw new BusinessException("A participant is already in a pending or allocated request for this slot");
        }
    }

    private void requireInterest(Long empId, Long gameId) {
        if (!interestRepo.existsByEmployeeIdAndGameId(empId, gameId))
            throw new BusinessException("Employee " + empId + " has not registered interest in this game");
    }

    private int getPlayCount(Long empId, Long gameId, int cycleNum) {
        return historyRepo.findByGameIdAndEmployeeIdAndCycleNumber(gameId, empId, cycleNum)
                .map(GameHistory::getPlayCount).orElse(0);
    }

    // ═══════════════════ ENTITY LOOKUPS ══════════════════════

    private Game findGame(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private GameSlot findSlot(Long id) {
        return gameSlotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
    }

    private Employee findEmployee(Long id) {
        return employeeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    // ═══════════════════ DTO MAPPERS ═════════════════════════

    private GameResponseDto toGameDto(Game g) {
        return GameResponseDto.builder()
                .id(g.getId()).name(g.getName()).active(g.isActive())
                .startHour(g.getStartHour()).endHour(g.getEndHour())
                .maxDurationMins(g.getMaxDurationMins())
                .maxPlayersPerSlot(g.getMaxPlayersPerSlot())
                .cancellationBeforeMins(g.getCancellationBeforeMins())
                .build();
    }

    private GameWaitlistResponseDto toWaitlistDto(GameWaitList w) {
        return GameWaitlistResponseDto.builder()
                .id(w.getId()).gameId(w.getGame().getId()).slotId(w.getSlot().getId())
                .gameName(w.getGame().getName())
                .slotStart(w.getSlot().getSlotStart()).slotEnd(w.getSlot().getSlotEnd())
                .requestedByName(fullName(w.getRequestedBy()))
                .appliedDateTime(w.getAppliedDateTime())
                .status(w.getStatus().name())
                .priorityScore(w.getPriorityScore())
                .participantNames(w.getParticipants().stream()
                        .map(p -> fullName(p.getEmployee())).toList())
                .build();
    }

    private GameBookingResponseDto toBookingDto(GameBooking b) {
        return GameBookingResponseDto.builder()
                .id(b.getId()).gameId(b.getGame().getId()).slotId(b.getSlot().getId())
                .gameName(b.getGame().getName())
                .slotStart(b.getSlot().getSlotStart()).slotEnd(b.getSlot().getSlotEnd())
                .bookedByName(fullName(b.getBookedBy()))
                .bookingDateTime(b.getBookingDateTime())
                .status(b.getStatus().name())
                .participantNames(b.getParticipants().stream()
                        .map(p -> fullName(p.getEmployee())).toList())
                .build();
    }

    private String fullName(Employee e) {
        return e.getFirstName() + " " + e.getLastName();
    }
}
