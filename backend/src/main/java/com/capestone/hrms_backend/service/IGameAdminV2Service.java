package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.GameV2RequestDto;
import com.capestone.hrms_backend.dto.response.GameSlotV2ResponseDto;
import com.capestone.hrms_backend.dto.response.GameV2ResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface IGameAdminV2Service {
    GameV2ResponseDto createGame(GameV2RequestDto dto);
    GameV2ResponseDto updateGame(Long gameId, GameV2RequestDto dto);
    GameV2ResponseDto toggleActive(Long gameId);
    List<GameV2ResponseDto> getAllGames();
    List<GameV2ResponseDto> getActiveGames();
    List<GameSlotV2ResponseDto> getComputedSlots(Long gameId, LocalDate date);
}