package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.GameBookingV2RequestDto;
import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.GameBookingV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameV2ResponseDto;
import com.capestone.hrms_backend.entity.game.v2.*;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.game.v2.*;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IEmailService;
import com.capestone.hrms_backend.service.IGamePlayV2Service;
import com.capestone.hrms_backend.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePlayV2ServiceImpl implements IGamePlayV2Service {

    private final GameV2Repository gameRepo;
    private final GameInterestV2Repository interestRepo;
    private final GameBookingV2Repository bookingRepo;
    private final GameHistoryV2Repository historyRepo;
    private final GameCycleV2Repository cycleRepo;
    private final EmployeeRepository employeeRepo;
    private final INotificationService notificationService;
    private final IEmailService emailService;

    //Interest

    @Override
    @Transactional
    public void registerInterest(Long gameId, Long employeeId) {
        GameV2 game = findGame(gameId);
        if (!game.isActive())
            throw new BusinessException("Game is not active");
        if (interestRepo.existsByEmployeeIdAndGameId(employeeId, gameId))
            throw new BusinessException("Already registered interest");

        GameInterestV2 i = new GameInterestV2();
        i.setEmployee(findEmployee(employeeId));
        i.setGame(game);
        interestRepo.save(i);
    }

    @Override
    @Transactional
    public void removeInterest(Long gameId, Long employeeId) {
        GameInterestV2 interest = interestRepo.findByEmployeeIdAndGameId(employeeId, gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest registration not found"));

        // block if employee has PENDING or ACTIVE booking for this game
        boolean hasLive = bookingRepo.findByParticipantEmployeeId(employeeId).stream()
                .anyMatch(b -> b.getGame().getId().equals(gameId)
                        && (b.getStatus() == GameBookingstatusV2.ACTIVE || b.getStatus() == GameBookingstatusV2.PENDING));
        if (hasLive)
            throw new BusinessException("Cancel active/pending bookings before removing interest");

        interestRepo.delete(interest);
    }

    @Override
    public List<GameV2ResponseDto> getMyInterests(Long employeeId) {
        return interestRepo.findByEmployeeId(employeeId).stream()
                .map(i -> toGameDto(i.getGame())).toList();
    }

    @Override
    public List<EmployeeLookupDto> getInterestedEmployees(Long gameId) {
        findGame(gameId);
        return interestRepo.findByGameId(gameId).stream()
                .map(GameInterestV2::getEmployee)
                .map(e -> new EmployeeLookupDto(
                        e.getId(), fullName(e),
                        e.getUser() != null ? e.getUser().getEmail() : null,
                        e.getDesignation(),
                        e.getDepartment() != null ? e.getDepartment().getName() : null,
                        e.getProfilePath()))
                .sorted(Comparator.comparing(EmployeeLookupDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    //Booking Requests
    @Override
    @Transactional
    public GameBookingV2ResponseDto submitRequest(Long gameId, GameBookingV2RequestDto dto, Long requestedById) {
        GameV2 game = findGame(gameId);
        if (!game.isActive())
            throw new BusinessException("Game is not active");
        Employee requester = findEmployee(requestedById);

        // participants: requester always included
        Set<Long> pids = new LinkedHashSet<>(dto.getParticipantIds());
        pids.add(requestedById);

        if (pids.size() > game.getMaxPlayersPerSlot())
            throw new BusinessException("Maximum " + game.getMaxPlayersPerSlot() + " participants allowed per slot");

        LocalDateTime slotStart = dto.getSlotStart();
        if (slotStart == null)
            throw new BusinessException("slotStart is required");
        if (slotStart.isBefore(LocalDateTime.now()))
            throw new BusinessException("Cannot request a slot in the past");
        LocalDateTime slotEnd = slotStart.plusMinutes(game.getMaxDurationMins());

        //Validate slot aligns with game operating hours
        LocalTime slotTime = slotStart.toLocalTime();
        if (slotTime.isBefore(game.getStartHour()) || slotEnd.toLocalTime().isAfter(game.getEndHour()))
            throw new BusinessException("Slot must be within game operating hours ("
                    + game.getStartHour() + " - " + game.getEndHour() + ")");

        //Slots start at startHour and repeat every maxDurationMins
        long minutesSinceStart = java.time.Duration.between(game.getStartHour(), slotTime).toMinutes();
        if (minutesSinceStart < 0 || minutesSinceStart % game.getMaxDurationMins() != 0)
            throw new BusinessException("slotStart must align with the game's slot grid (every "
                    + game.getMaxDurationMins() + " minutes starting from " + game.getStartHour() + ")");

        //Validate request is before cancellation cutoff
        LocalDateTime cutoff = slotStart.minusMinutes(game.getCancellationBeforeMins());
        if (LocalDateTime.now().isAfter(cutoff))
            throw new BusinessException("Request must be submitted at least "
                    + game.getCancellationBeforeMins() + " minutes before slot start");

        //validate all participants have registered interest
        for (Long pid : pids) {
            if (!interestRepo.existsByEmployeeIdAndGameId(pid, gameId))
                throw new BusinessException("Employee " + pid + " has not registered interest in this game");
        }

        // PENDING requests are allowed; the check is enforced again at allocation time.
        LocalDateTime dayStart = slotStart.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        for (Long pid : pids) {
            if (bookingRepo.hasActiveBookingOnDate(pid, dayStart, dayEnd)) {
                Employee emp = findEmployee(pid);
                throw new BusinessException(fullName(emp)
                        + " already has an active booking on " + slotStart.toLocalDate());
            }
        }

        for (Long pid : pids) {
            if (bookingRepo.hasPendingOrActiveForSlot(pid, gameId, slotStart)) {
                Employee emp = findEmployee(pid);
                throw new BusinessException(fullName(emp)
                        + " already has a pending/active request for this slot");
            }
        }

        // block if slot already has an ACTIVE or COMPLETED booking
        if (bookingRepo.existsByGameIdAndSlotStartAndStatusIn(gameId, slotStart,
                List.of(GameBookingstatusV2.ACTIVE, GameBookingstatusV2.COMPLETED)))
            throw new BusinessException("This slot is already booked");

        // priority = 5 − max(playCount among participants). Can go negative.
        int cycleNum = getOrCreateCycleNumber(game);
        int playcount = 0;
        boolean allZero = true;
        for (Long pid : pids) {
            int pc = historyRepo.findByGameIdAndEmployeeIdAndCycleNumber(gameId, pid, cycleNum)
                    .map(GameHistoryV2::getPlayCount).orElse(0);
            playcount += pc;
            if (pc > 0)
                allZero = false;
        }
        int priorityScore = 5 - playcount;

        GameBookingV2 bk = new GameBookingV2();
        bk.setGame(game);
        bk.setSlotStart(slotStart);
        bk.setSlotEnd(slotEnd);
        bk.setBookedBy(requester);
        bk.setBookingDateTime(LocalDateTime.now());
        bk.setPriorityScore(priorityScore);

        //If all have zero play, book active immediately
        if (allZero) {
            bk.setStatus(GameBookingstatusV2.ACTIVE);
        } else {
            bk.setStatus(GameBookingstatusV2.PENDING);
        }

        bookingRepo.save(bk);

        //add participants
        for (Long pid : pids) {
            GameBookingParticipantV2 bp = new GameBookingParticipantV2();
            bp.setBooking(bk);
            bp.setEmployee(findEmployee(pid));
            bk.getParticipants().add(bp);
        }
        bookingRepo.save(bk);

        // notify if instantly booked
        if (bk.getStatus() == GameBookingstatusV2.ACTIVE) {
            notifyParticipants(bk, "Game Booking Confirmed — " + game.getName(),
                    "Your slot for " + game.getName() + " on " + slotStart.toLocalDate()
                            + " (" + slotStart.toLocalTime() + " - " + slotEnd.toLocalTime()
                            + ") has been confirmed.");
        }

        return toBookingDto(bk);
    }

    @Override
    @Transactional
    public void cancelRequest(Long bookingId, Long employeeId) {
        GameBookingV2 bk = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!bk.getBookedBy().getId().equals(employeeId))
            throw new BusinessException("Only the requester can cancel");

        if (bk.getStatus() != GameBookingstatusV2.PENDING && bk.getStatus() != GameBookingstatusV2.ACTIVE)
            throw new BusinessException("Only PENDING or ACTIVE bookings can be cancelled");

        // if ACTIVE, check cancellation window
        if (bk.getStatus() == GameBookingstatusV2.ACTIVE) {
            int leadMins = bk.getGame().getCancellationBeforeMins();
            if (LocalDateTime.now().isAfter(bk.getSlotStart().minusMinutes(leadMins)))
                throw new BusinessException("Cancellation window has passed. Must cancel at least "
                        + leadMins + " minutes before slot start");
        }

        boolean wasActive = bk.getStatus() == GameBookingstatusV2.ACTIVE;
        bk.setStatus(GameBookingstatusV2.CANCELLED);
        bookingRepo.save(bk);

        // notify participants
        notifyParticipants(bk, "Game Booking Cancelled — " + bk.getGame().getName(),
                "Booking for " + bk.getGame().getName() + " on " + bk.getSlotStart().toLocalDate()
                        + " (" + bk.getSlotStart().toLocalTime() + " - " + bk.getSlotEnd().toLocalTime()
                        + ") has been cancelled.");

        // if an ACTIVE booking was cancelled, re-run allocation to promote next best PENDING
        if (wasActive) {
            allocatePendingSlots();
        }
    }

    @Override
    public List<GameBookingV2ResponseDto> getMyRequests(Long employeeId) {
        return bookingRepo.findByBookedByIdOrderBySlotStartDesc(employeeId).stream()
                .map(this::toBookingDto).toList();
    }

    @Override
    public List<GameBookingV2ResponseDto> getMyBookings(Long employeeId) {
        return bookingRepo.findByParticipantEmployeeId(employeeId).stream()
                .map(this::toBookingDto).toList();
    }

    
    @Override
    @Transactional
    public void allocatePendingSlots() {
        LocalDateTime cutoff = LocalDateTime.now().plusHours(1);

        List<GameBookingV2> pendingWithinWindow = bookingRepo
                .findByStatusAndSlotStartLessThanEqual(GameBookingstatusV2.PENDING, cutoff);

        if (pendingWithinWindow.isEmpty())
            return;

        // group by (gameId, slotStart)
        Map<String, List<GameBookingV2>> grouped = pendingWithinWindow.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getGame().getId() + "_" + b.getSlotStart().toString()));

        for (Map.Entry<String, List<GameBookingV2>> entry : grouped.entrySet()) {
            List<GameBookingV2> candidates = entry.getValue();
            Long gameId = candidates.get(0).getGame().getId();
            LocalDateTime slotStart = candidates.get(0).getSlotStart();

            // skip if this slot already has an ACTIVE/COMPLETED booking
            if (bookingRepo.existsByGameIdAndSlotStartAndStatusIn(gameId, slotStart,
                    List.of(GameBookingstatusV2.ACTIVE, GameBookingstatusV2.COMPLETED))) {
                // expire all pending for this slot
                for (GameBookingV2 c : candidates) {
                    c.setStatus(GameBookingstatusV2.EXPIRED);
                    bookingRepo.save(c);
                    notifyParticipants(c, "Game Request Not Allocated — " + c.getGame().getName(),
                            "Your request for " + c.getGame().getName()
                                    + " on " + slotStart.toLocalDate() + " could not be allocated.");
                }
                continue;
            }

            //sort by priorityScore DESC (highest = best), then appliedDateTime
            candidates.sort(Comparator
                    .comparingInt(GameBookingV2::getPriorityScore).reversed()
                    .thenComparing(GameBookingV2::getBookingDateTime));

            // pick the first eligible candidate
            LocalDateTime dayStart = slotStart.toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            GameBookingV2 winner = null;
            List<GameBookingV2> losers = new ArrayList<>();

            for (GameBookingV2 candidate : candidates) {
                if (winner != null) {
                    losers.add(candidate);
                    continue;
                }
                // check one-active-per-day: skip if any participant already has ACTIVE booking today
                boolean eligible = true;
                for (GameBookingParticipantV2 p : candidate.getParticipants()) {
                    if (bookingRepo.hasActiveBookingOnDate(
                            p.getEmployee().getId(), dayStart, dayEnd)) {
                        eligible = false;
                        break;
                    }
                }
                if (eligible) {
                    winner = candidate;
                } else {
                    losers.add(candidate);
                }
            }

            if (winner != null) {
                winner.setStatus(GameBookingstatusV2.ACTIVE);
                bookingRepo.save(winner);

                notifyParticipants(winner, "Game Booking Confirmed — " + winner.getGame().getName(),
                        "Your slot for " + winner.getGame().getName() + " on " + slotStart.toLocalDate()
                                + " (" + slotStart.toLocalTime() + " - " + winner.getSlotEnd().toLocalTime()
                                + ") has been confirmed.");
            }

            //expire the rest
            for (GameBookingV2 loser : losers) {
                loser.setStatus(GameBookingstatusV2.EXPIRED);
                bookingRepo.save(loser);
                notifyParticipants(loser, "Game Request Not Allocated — " + loser.getGame().getName(),
                        "Your request for " + loser.getGame().getName()
                                + " on " + slotStart.toLocalDate() + " could not be allocated.");
            }

            log.info("Allocated slot {} for game {} to booking {}, expired {} others",
                    slotStart, gameId, winner.getId(), candidates.size() - 1);
        }
    }

    //Marks completed, increment cycle count, check cycle rollover
    @Override
    @Transactional
    public void completeExpiredBookings() {
        List<GameBookingV2> completable = bookingRepo.findCompletableBookings(LocalDateTime.now());

        for (GameBookingV2 bk : completable) {
            bk.setStatus(GameBookingstatusV2.COMPLETED);
            bookingRepo.save(bk);

            GameV2 game = bk.getGame();
            int cycleNum = getOrCreateCycleNumber(game);
            incrementPlayCounts(bk, game, cycleNum);
            checkCycleRollover(game);
        }
    }

    /**
     * Marks leftover PENDING entries as EXPIRED if their slotStart has passed.
     */
    @Override
    @Transactional
    public void expireStaleRequests() {
        List<GameBookingV2> stale = bookingRepo.findExpiredPendingBookings(LocalDateTime.now());
        for (GameBookingV2 bk : stale) {
            bk.setStatus(GameBookingstatusV2.EXPIRED);
            bookingRepo.save(bk);
        }
    }

    //Helper functions:

    private int getOrCreateCycleNumber(GameV2 game) {
        return cycleRepo.findByGameIdAndEndedAtIsNull(game.getId())
                .map(GameCycleV2::getCycleNumber)
                .orElseGet(() -> {
                    GameCycleV2 c = new GameCycleV2();
                    c.setGame(game);
                    c.setCycleNumber(1);
                    c.setStartedAt(LocalDateTime.now());
                    cycleRepo.save(c);
                    return 1;
                });
    }

    private void incrementPlayCounts(GameBookingV2 bk, GameV2 game, int cycleNum) {
        for (GameBookingParticipantV2 bp : bk.getParticipants()) {
            GameHistoryV2 h = historyRepo
                    .findByGameIdAndEmployeeIdAndCycleNumber(game.getId(), bp.getEmployee().getId(), cycleNum)
                    .orElseGet(() -> {
                        GameHistoryV2 nh = new GameHistoryV2();
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

    private void checkCycleRollover(GameV2 game) {
        GameCycleV2 active = cycleRepo.findByGameIdAndEndedAtIsNull(game.getId()).orElse(null);
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

            GameCycleV2 next = new GameCycleV2();
            next.setGame(game);
            next.setCycleNumber(active.getCycleNumber() + 1);
            next.setStartedAt(LocalDateTime.now());
            cycleRepo.save(next);

            log.info("Cycle rollover for game {}: {} -> {}",
                    game.getName(), active.getCycleNumber(), next.getCycleNumber());
        }
    }

    private void notifyParticipants(GameBookingV2 bk, String subject, String body) {
        for (GameBookingParticipantV2 bp : bk.getParticipants()) {
            notificationService.create(bp.getEmployee().getId(), subject, body);
            try {
                if (bp.getEmployee().getUser() != null) {
                    emailService.send(bp.getEmployee().getUser().getEmail(), subject, body);
                }
            } catch (Exception e) {
                log.warn("Failed to send email to {}: {}", bp.getEmployee().getId(), e.getMessage());
            }
        }
    }

    private GameV2 findGame(Long id) {
        return gameRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private Employee findEmployee(Long id) {
        return employeeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private String fullName(Employee e) {
        return e.getFirstName() + " " + e.getLastName();
    }

    private GameV2ResponseDto toGameDto(GameV2 g) {
        return GameV2ResponseDto.builder()
                .id(g.getId()).name(g.getName()).active(g.isActive())
                .startHour(g.getStartHour()).endHour(g.getEndHour())
                .maxDurationMins(g.getMaxDurationMins())
                .maxPlayersPerSlot(g.getMaxPlayersPerSlot())
                .cancellationBeforeMins(g.getCancellationBeforeMins())
                .build();
    }

    private GameBookingV2ResponseDto toBookingDto(GameBookingV2 b) {
        return GameBookingV2ResponseDto.builder()
                .id(b.getId()).gameId(b.getGame().getId())
                .gameName(b.getGame().getName())
                .slotStart(b.getSlotStart()).slotEnd(b.getSlotEnd())
                .bookedByName(fullName(b.getBookedBy()))
                .bookingDateTime(b.getBookingDateTime())
                .priorityScore(b.getPriorityScore())
                .status(b.getStatus().name())
                .participantNames(b.getParticipants().stream()
                        .map(p -> fullName(p.getEmployee())).toList())
                .build();
    }
}