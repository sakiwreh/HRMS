package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameSlot;
import com.capestone.hrms_backend.entity.game.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GameSlotRepository extends JpaRepository<GameSlot,Long> {
    List<GameSlot> findByGameIdAndSlotDate(Long gameId, LocalDate slotDate);

    List<GameSlot> findByGameIdAndSlotDateBetweenAndStatus(Long gameId, LocalDate from, LocalDate to, SlotStatus status);

    List<GameSlot> findByGameIdAndStatusAndSlotDateGreaterThanEqual(Long gameId, SlotStatus status, LocalDate from);

    List<GameSlot> findByGameIdAndSlotDateBetween(Long gameId, LocalDate from, LocalDate to);

    boolean existsByGameIdAndSlotDate(Long gameId, LocalDate slotDate);
}
