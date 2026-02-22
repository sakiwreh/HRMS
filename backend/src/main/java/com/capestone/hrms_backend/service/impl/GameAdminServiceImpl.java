package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.GameRequestDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameSlotResponseDto;
import com.capestone.hrms_backend.entity.game.Game;
import com.capestone.hrms_backend.entity.game.GameSlot;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.game.GameRepository;
import com.capestone.hrms_backend.repository.game.GameSlotRepository;
import com.capestone.hrms_backend.service.IGameAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameAdminServiceImpl implements IGameAdminService {

    private final GameRepository gameRepo;
    private final GameSlotRepository slotRepo;

    @Override
    @Transactional
    public GameResponseDto createGame(GameRequestDto dto) {
        validateHours(dto);
        Game game = new Game();
        applyFields(game, dto);
        return toDto(gameRepo.save(game));
    }

    @Override
    @Transactional
    public GameResponseDto updateGame(Long gameId, GameRequestDto dto) {
        validateHours(dto);
        Game game = findGame(gameId);
        applyFields(game, dto);
        return toDto(gameRepo.save(game));
    }

    @Override
    @Transactional
    public GameResponseDto toggleActive(Long gameId) {
        Game game = findGame(gameId);
        game.setActive(!game.isActive());
        return toDto(gameRepo.save(game));
    }

    @Override
    public List<GameResponseDto> getAllGames() {
        return gameRepo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<GameResponseDto> getActiveGames() {
        return gameRepo.findByActiveTrue().stream().map(this::toDto).toList();
    }

    @Override
    public List<GameSlotResponseDto> getSlots(Long gameId, LocalDate from, LocalDate to) {
        Game game = findGame(gameId);
        return slotRepo.findByGameIdAndSlotDateBetween(gameId, from, to).stream()
                .map(s -> toSlotDto(s, game))
                .toList();
    }

    @Override
    @Transactional
    public List<Long> generateSlotsForDate(Long gameId, LocalDate date) {
        Game game = findGame(gameId);

        if (!game.isActive())
            throw new BusinessException("Cannot generate slots for an inactive game");
        if (date.isBefore(LocalDate.now()))
            throw new BusinessException("Cannot generate slots for a past date");
        if (slotRepo.existsByGameIdAndSlotDate(gameId, date))
            throw new BusinessException("Slots already generated for this date");

        LocalTime cursor = game.getStartHour();
        LocalTime end = game.getEndHour();
        int mins = game.getMaxDurationMins();

        List<Long> slotIds = new ArrayList<>();
        while (!cursor.plusMinutes(mins).isAfter(end)) {
            GameSlot slot = new GameSlot();
            slot.setGame(game);
            slot.setSlotDate(date);
            slot.setSlotStart(date.atTime(cursor));
            slot.setSlotEnd(date.atTime(cursor.plusMinutes(mins)));
            slotRepo.save(slot);
            slotIds.add(slot.getId());
            cursor = cursor.plusMinutes(mins);
        }

        log.info("Generated {} slots for game {} on {}", slotIds.size(), game.getName(), date);
        return slotIds;
    }

    private Game findGame(Long id) {
        return gameRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    private void validateHours(GameRequestDto dto) {
        if (dto.getStartHour().equals(dto.getEndHour()))
            return; // 24-hour availability
        if (!dto.getStartHour().isBefore(dto.getEndHour()))
            throw new BusinessException("Start hour must be before end hour");
    }

    private void applyFields(Game game, GameRequestDto dto) {
        game.setName(dto.getName());
        game.setStartHour(dto.getStartHour());
        game.setEndHour(dto.getEndHour());
        game.setMaxDurationMins(dto.getMaxDurationMins());
        game.setMaxPlayersPerSlot(dto.getMaxPlayersPerSlot());
        game.setCancellationBeforeMins(dto.getCancellationBeforeMins());
        game.setSlotGenerationDays(dto.getSlotGenerationDays());
    }

    private GameResponseDto toDto(Game g) {
        return GameResponseDto.builder()
                .id(g.getId()).name(g.getName()).active(g.isActive())
                .startHour(g.getStartHour()).endHour(g.getEndHour())
                .maxDurationMins(g.getMaxDurationMins())
                .maxPlayersPerSlot(g.getMaxPlayersPerSlot())
                .cancellationBeforeMins(g.getCancellationBeforeMins())
                .slotGenerationDays(g.getSlotGenerationDays())
                .build();
    }

    private GameSlotResponseDto toSlotDto(GameSlot s, Game g) {
        return GameSlotResponseDto.builder()
                .id(s.getId()).gameId(g.getId()).gameName(g.getName())
                .slotDate(s.getSlotDate())
                .slotStart(s.getSlotStart()).slotEnd(s.getSlotEnd())
                .capacity(g.getMaxPlayersPerSlot())
                .bookedCount(s.getBookedCount())
                .status(s.getStatus().name())
                .build();
    }
}