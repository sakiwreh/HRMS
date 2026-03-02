package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.GameV2RequestDto;
import com.capestone.hrms_backend.dto.response.GameSlotV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameV2ResponseDto;
import com.capestone.hrms_backend.entity.game.v2.GameBookingV2;
import com.capestone.hrms_backend.entity.game.v2.GameBookingstatusV2;
import com.capestone.hrms_backend.entity.game.v2.GameV2;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.game.v2.GameBookingV2Repository;
import com.capestone.hrms_backend.repository.game.v2.GameV2Repository;
import com.capestone.hrms_backend.service.IGameAdminV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameAdminV2ServiceImpl implements IGameAdminV2Service {

    private final GameV2Repository gameRepo;
    private final GameBookingV2Repository bookingRepo;

    @Override
    @Transactional
    public GameV2ResponseDto createGame(GameV2RequestDto dto) {
        validateHours(dto);
        GameV2 game = new GameV2();
        applyFields(game, dto);
        return toDto(gameRepo.save(game));
    }

    @Override
    @Transactional
    public GameV2ResponseDto updateGame(Long gameId, GameV2RequestDto dto) {
        validateHours(dto);
        GameV2 game = findGame(gameId);
        applyFields(game, dto);
        return toDto(gameRepo.save(game));
    }

    @Override
    @Transactional
    public GameV2ResponseDto toggleActive(Long gameId) {
        GameV2 game = findGame(gameId);
        game.setActive(!game.isActive());
        return toDto(gameRepo.save(game));
    }

    @Override
    public List<GameV2ResponseDto> getAllGames() {
        return gameRepo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<GameV2ResponseDto> getActiveGames() {
        return gameRepo.findByActiveTrue().stream().map(this::toDto).toList();
    }

    //Compute slots from Game config, AVAILABLE, REQUESTED, BOOKED
    @Override
    public List<GameSlotV2ResponseDto> getComputedSlots(Long gameId, LocalDate date) {
        GameV2 game = findGame(gameId);

        LocalDateTime dayStart = date.atTime(game.getStartHour());
        LocalDateTime dayEnd = date.atTime(game.getEndHour());

        // fetch all bookings for this game on this date
        List<GameBookingV2> dayBookings = bookingRepo
                .findByGameIdAndSlotStartBetween(gameId, dayStart, dayEnd);

        // group by slotStart
        Map<LocalDateTime, List<GameBookingV2>> bySlot = dayBookings.stream()
                .collect(Collectors.groupingBy(GameBookingV2::getSlotStart));

        List<GameSlotV2ResponseDto> slots = new ArrayList<>();
        LocalTime cursor = game.getStartHour();
        int mins = game.getMaxDurationMins();

        while (!cursor.plusMinutes(mins).isAfter(game.getEndHour())) {
            LocalDateTime slotStart = date.atTime(cursor);
            LocalDateTime slotEnd = date.atTime(cursor.plusMinutes(mins));

            List<GameBookingV2> bookingsForSlot = bySlot.getOrDefault(slotStart, List.of());

            boolean hasActiveOrCompleted = bookingsForSlot.stream()
                    .anyMatch(b -> b.getStatus() == GameBookingstatusV2.ACTIVE
                            || b.getStatus() == GameBookingstatusV2.COMPLETED);

            long pendingCount = bookingsForSlot.stream()
                    .filter(b -> b.getStatus() == GameBookingstatusV2.PENDING)
                    .count();

            String status;
            if (hasActiveOrCompleted) {
                status = "BOOKED";
            } else if (pendingCount > 0) {
                status = "REQUESTED";
            } else {
                status = "AVAILABLE";
            }

            slots.add(GameSlotV2ResponseDto.builder()
                    .gameId(game.getId())
                    .gameName(game.getName())
                    .slotStart(slotStart)
                    .slotEnd(slotEnd)
                    .status(status)
                    .pendingCount((int) pendingCount)
                    .build());

            cursor = cursor.plusMinutes(mins);
        }

        return slots;
    }

    //Helper functions

    private GameV2 findGame(Long id) {
        return gameRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private void validateHours(GameV2RequestDto dto) {
        if (dto.getStartHour().equals(dto.getEndHour()))
            return; // 24-hour
        if (!dto.getStartHour().isBefore(dto.getEndHour()))
            throw new BusinessException("Start hour must be before end hour");
    }

    private void applyFields(GameV2 game, GameV2RequestDto dto) {
        game.setName(dto.getName());
        game.setStartHour(dto.getStartHour());
        game.setEndHour(dto.getEndHour());
        game.setMaxDurationMins(dto.getMaxDurationMins());
        game.setMaxPlayersPerSlot(dto.getMaxPlayersPerSlot());
        game.setCancellationBeforeMins(dto.getCancellationBeforeMins());
    }

    private GameV2ResponseDto toDto(GameV2 g) {
        return GameV2ResponseDto.builder()
                .id(g.getId()).name(g.getName()).active(g.isActive())
                .startHour(g.getStartHour()).endHour(g.getEndHour())
                .maxDurationMins(g.getMaxDurationMins())
                .maxPlayersPerSlot(g.getMaxPlayersPerSlot())
                .cancellationBeforeMins(g.getCancellationBeforeMins())
                .build();
    }
}