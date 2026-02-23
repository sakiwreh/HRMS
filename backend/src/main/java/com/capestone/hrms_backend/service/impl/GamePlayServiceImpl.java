package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.GameWaitlistRequestDto;
import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.GameBookingResponseDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameWaitlistResponseDto;
import com.capestone.hrms_backend.entity.game.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.game.*;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.IGamePlayService;
import com.capestone.hrms_backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePlayServiceImpl implements IGamePlayService {

    private final GameRepository gameRepo;
    private final GameSlotRepository slotRepo;
    private final GameInterestRepository interestRepo;
    private final GameCycleRepository cycleRepo;
    private final GameHistoryRepository historyRepo;
    private final GameWaitlistRepository waitlistRepo;
    private final GameWaitlistParticipantRepository waitlistParticipantRepo;
    private final GameBookingRepository bookingRepo;
    private final EmployeeRepository employeeRepo;
    private final INotificationService notificationService;
    private final IEmailService emailService;

    // ═══════════════════════ INTEREST ═══════════════════════

    @Override
    @Transactional
    public void registerInterest(Long gameId, Long employeeId) {
        Game game = findGame(gameId);
        if (!game.isActive())
            throw new BusinessException("Game is not active");

        Employee emp = findEmployee(employeeId);
        if (interestRepo.existsByEmployeeIdAndGameId(employeeId, gameId))
            throw new BusinessException("Already registered interest");

        GameInterest interest = new GameInterest();
        interest.setEmployee(emp);
        interest.setGame(game);
        interestRepo.save(interest);
    }

    @Override
    @Transactional
    public void removeInterest(Long gameId, Long employeeId) {
        GameInterest interest = interestRepo.findByEmployeeIdAndGameId(employeeId, gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest registration not found"));

        // prevent removing interest while the user has pending waitlist requests
        boolean hasPending = waitlistRepo
                .findByRequestedByIdAndStatus(employeeId, WaitlistStatus.WAIT)
                .stream().anyMatch(w -> w.getGame().getId().equals(gameId));
        if (hasPending)
            throw new BusinessException("Cancel pending requests before removing interest");

        // prevent removing interest while holding an active booking for this game
        boolean hasActive = bookingRepo.findByParticipantEmployeeId(employeeId).stream()
                .anyMatch(b -> b.getGame().getId().equals(gameId) && b.getStatus() == BookingStatus.ACTIVE);
        if (hasActive)
            throw new BusinessException("Cannot remove interest while you have an active booking for this game");

        interestRepo.delete(interest);
    }

    @Override
    public List<GameResponseDto> getMyInterests(Long employeeId) {
        return interestRepo.findByEmployeeId(employeeId).stream()
                .map(i -> toGameDto(i.getGame())).toList();
    }

    @Override
    public List<EmployeeLookupDto> getInterestedEmployees(Long gameId) {
        findGame(gameId);
        return interestRepo.findByGameId(gameId).stream()
                .map(GameInterest::getEmployee)
                .map(e -> new EmployeeLookupDto(
                        e.getId(),
                        fullName(e),
                        e.getUser() != null ? e.getUser().getEmail() : null,
                        e.getDesignation(),
                        e.getDepartment() != null ? e.getDepartment().getName() : null))
                .sorted(Comparator.comparing(EmployeeLookupDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    @Transactional
    public GameWaitlistResponseDto submitRequest(GameWaitlistRequestDto dto, Long requestedById) {
        log.info("DTO: {}",dto);
        GameSlot slot = findSlot(dto.getSlotId());
        Game game = slot.getGame();

        validateSlotForRequest(slot, game);
        log.info("slot: {}",slot);
        log.info("Gme: {}",game);

        Employee requester = findEmployee(requestedById);
        requireInterest(requestedById, game.getId());

        // requester is always a participant
        Set<Long> pids = new LinkedHashSet<>(dto.getParticipantIds());
        pids.add(requestedById);

        // max participants per booking from game config
        if (pids.size() > game.getMaxPlayersPerSlot())
            throw new BusinessException("Maximum " + game.getMaxPlayersPerSlot()
                    + " participants allowed per booking");

        log.info("Validating participants");
        validateParticipants(pids, game, slot);
        log.info("PArticipants validated");
        int cycleNum = getOrCreateCycleNumber(game);
        int priority = pids.stream()
                .mapToInt(pid -> getPlayCount(pid, game.getId(), cycleNum))
                .max().orElse(0);

        // create waitlist entry with participants
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
        return waitlistRepo.findByRequestedByIdOrderByAppliedDateTimeDesc(employeeId).stream()
                .map(this::toWaitlistDto).toList();
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long employeeId) {
        GameBooking bk = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!bk.getBookedBy().getId().equals(employeeId))
            throw new BusinessException("Only the person who booked can cancel");
        if (bk.getStatus() != BookingStatus.ACTIVE)
            throw new BusinessException("Booking is not active");

        // cancellation window check
        int leadMins = bk.getGame().getCancellationBeforeMins();
        if (LocalDateTime.now().isAfter(bk.getSlot().getSlotStart().minusMinutes(leadMins)))
            throw new BusinessException("Cancellation window has passed. Must cancel at least "
                    + leadMins + " minutes before slot start");

        bk.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(bk);

        // free up slot capacity and re-trigger allocator
        GameSlot slot = bk.getSlot();
        slot.setBookedCount(Math.max(0, slot.getBookedCount() - bk.getParticipants().size()));
        if (slot.getStatus() == SlotStatus.LOCKED)
            slot.setStatus(SlotStatus.AVAILABLE);
        slotRepo.save(slot);

        // notify participants about cancellation
        String cancelSubject = "Game Booking Cancelled — " + bk.getGame().getName();
        String cancelBody = "Your booking for " + bk.getGame().getName()
                + " on " + slot.getSlotStart().toLocalDate()
                + " (" + slot.getSlotStart().toLocalTime() + " - " + slot.getSlotEnd().toLocalTime()
                + ") has been cancelled.";
        for (GameBookingParticipant bp : bk.getParticipants()) {
            notificationService.create(bp.getEmployee().getId(), cancelSubject, cancelBody);
        }

        allocateSlot(slot.getId());
    }

    @Override
    public List<GameBookingResponseDto> getMyBookings(Long employeeId) {
        return bookingRepo.findByParticipantEmployeeId(employeeId).stream()
                .map(this::toBookingDto).toList();
    }

    @Override
    public List<GameBookingResponseDto> getBookingsForSlot(Long slotId) {
        findSlot(slotId);
        return bookingRepo.findBySlotId(slotId).stream()
                .map(this::toBookingDto).toList();
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
                slotRepo.save(bk.getSlot());
            }

            checkCycleRollover(game);
        }
    }

    //Marks leftover WAIT entries as EXPIRED after their slot time has passed.
    @Override
    @Transactional
    public void finalizeExpiredWaitlistEntries() {
        List<GameWaitList> expired = waitlistRepo.findExpiredWaitEntries(LocalDateTime.now());
        for (GameWaitList entry : expired) {
            entry.setStatus(WaitlistStatus.EXPIRED);
            waitlistRepo.save(entry);
        }
    }

@Override
@Transactional
public void allocateSlot(Long slotId) {
    GameSlot slot = findSlot(slotId);
    Game game = slot.getGame();

    if (slot.getStatus() == SlotStatus.COMPLETED || slot.getStatus() == SlotStatus.CANCELLED)
        return;

    List<GameWaitList> queue = waitlistRepo
            .findBySlotIdAndStatusOrderByPriorityScoreAscAppliedDateTimeAscIdAsc(slotId, WaitlistStatus.WAIT);

    int capacity = game.getMaxPlayersPerSlot();
    int occupied = slot.getBookedCount();

    for (GameWaitList entry : queue) {
        int needed = entry.getParticipants().size();
        if (occupied + needed > capacity)
            continue;

        // check if any participant already has an active booking for ANY game today
        boolean conflict = entry.getParticipants().stream()
                .anyMatch(p -> bookingRepo.hasActiveBookingOnDate(
                        p.getEmployee().getId(), slot.getSlotDate()));
        if (conflict)
            continue;

        // convert waitlist entry → booking
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

        // send notification + email to all participants
        String subject = "Game Booking Confirmed — " + game.getName();
        String body = "You have been allocated a slot for " + game.getName()
                + " on " + slot.getSlotStart().toLocalDate()
                + " from " + slot.getSlotStart().toLocalTime()
                + " to " + slot.getSlotEnd().toLocalTime() + ".";
        for (GameBookingParticipant bp : bk.getParticipants()) {
            notificationService.create(bp.getEmployee().getId(), subject, body);
            try {
                emailService.send(bp.getEmployee().getUser().getEmail(), subject, body);
            } catch (Exception e) {
                log.warn("Failed to send game booking email to {}: {}",
                        bp.getEmployee().getUser().getEmail(), e.getMessage());
            }
        }

        if (occupied >= capacity)
            break;
    }

    slot.setBookedCount(occupied);
    if (occupied >= capacity)
        slot.setStatus(SlotStatus.LOCKED);
    slotRepo.save(slot);

    // mark remaining WAIT entries as EXPIRED (they weren't allocated)
    List<GameWaitList> remaining = waitlistRepo
            .findBySlotIdAndStatusOrderByPriorityScoreAscAppliedDateTimeAscIdAsc(slotId, WaitlistStatus.WAIT);
    for (GameWaitList leftover : remaining) {
        leftover.setStatus(WaitlistStatus.EXPIRED);
        waitlistRepo.save(leftover);

        // notify the requestor that their request was not allocated
        String expSubject = "Game Request Not Allocated — " + game.getName();
        String expBody = "Your request for " + game.getName()
                + " on " + slot.getSlotStart().toLocalDate()
                + " (" + slot.getSlotStart().toLocalTime() + " - " + slot.getSlotEnd().toLocalTime()
                + ") could not be allocated.";
        notificationService.create(leftover.getRequestedBy().getId(), expSubject, expBody);
    }
}

    private void checkCycleRollover(Game game) {
        GameCycle active = cycleRepo.findByGameIdAndEndedAtIsNull(game.getId()).orElse(null);
        if (active == null)
            return;

        long interested = interestRepo.countByGameId(game.getId());
        if (interested == 0)
            return;

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

    // Returns the active cycle number, creating cycle 1 if none exists.
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


    private void validateSlotForRequest(GameSlot slot, Game game) {
        if (slot.getStatus() != SlotStatus.AVAILABLE)
            throw new BusinessException("Slot is not available for booking");
        if (slot.getSlotStart().isBefore(LocalDateTime.now()))
            throw new BusinessException("Cannot request a slot that has already started");

        // block requests after cutoff (cutoff = slotStart - cancellationBeforeMins)
        LocalDateTime cutoff = slot.getSlotStart().minusMinutes(game.getCancellationBeforeMins());
        if (LocalDateTime.now().isAfter(cutoff))
            throw new BusinessException("Request window has closed. Requests must be placed at least "
                    + game.getCancellationBeforeMins() + " minutes before slot start");
    }

    private void validateParticipants(Set<Long> pids, Game game, GameSlot slot) {
        for (Long pid : pids) {
            requireInterest(pid, game.getId());
            log.info("{}",pid);

            // one booking per employee per day across ALL games
            if (bookingRepo.hasActiveBookingOnDate(pid, slot.getSlotDate()))
                throw new BusinessException("A participant already has an active booking for this day");
            log.info("Actvie booking checked");
        }

        if (waitlistRepo.existsBySlotIdAndRequestedByIdAndStatus(slot.getId(), pids.iterator().next(),
                WaitlistStatus.WAIT))
            throw new BusinessException("You already have a pending request for this slot");
        log.info("Exists by slotid and requestedbyid checked");

        // ensure no participant is already in another WAIT/ALLOCATED entry on same slot
        List<Long> taken = waitlistParticipantRepo.findActiveParticipantEmployeeIdsBySlotId(slot.getId());
        for (Long pid : pids) {
            if (taken.contains(pid))
                throw new BusinessException("A participant is already in a pending or allocated request for this slot");
        }
        log.info("Check no participant in wait or allocated");
    }

    private void requireInterest(Long empId, Long gameId) {
        if (!interestRepo.existsByEmployeeIdAndGameId(empId, gameId))
            throw new BusinessException("Employee " + empId + " has not registered interest in this game");
    }

    private int getPlayCount(Long empId, Long gameId, int cycleNum) {
        return historyRepo.findByGameIdAndEmployeeIdAndCycleNumber(gameId, empId, cycleNum)
                .map(GameHistory::getPlayCount).orElse(0);
    }


    private Game findGame(Long id) {
        return gameRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private GameSlot findSlot(Long id) {
        return slotRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
    }

    private Employee findEmployee(Long id) {
        return employeeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }


    private GameResponseDto toGameDto(Game g) {
        return GameResponseDto.builder()
                .id(g.getId()).name(g.getName()).active(g.isActive())
                .startHour(g.getStartHour()).endHour(g.getEndHour())
                .maxDurationMins(g.getMaxDurationMins())
                .maxPlayersPerSlot(g.getMaxPlayersPerSlot())
                .cancellationBeforeMins(g.getCancellationBeforeMins())
                .slotGenerationDays(g.getSlotGenerationDays())
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