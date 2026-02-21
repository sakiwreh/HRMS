package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.GameRequestDto;
import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.dto.response.GameSlotResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface IGameAdminService {
    public GameResponseDto createGame(GameRequestDto dto);
    public GameResponseDto updateGame(Long gameId, GameRequestDto dto);
    public GameResponseDto toggleActive(Long gameId);
    public List<GameResponseDto> getAllGames();
    public List<GameResponseDto> getActiveGames();
    public List<GameSlotResponseDto> getSlots(Long gameId, LocalDate from, LocalDate to);
    public void generateSlotsForDate(Long gameId, LocalDate date);

}
