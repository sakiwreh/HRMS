package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameSlot;
import com.capestone.hrms_backend.entity.game.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface GameSlotRepository extends JpaRepository<GameSlot, Long> {

    List<GameSlot> findByGameIdAndSlotDate(Long gameId, LocalDate slotDate);

    List<GameSlot> findByGameIdAndSlotDateBetweenAndStatus(
            Long gameId, LocalDate from, LocalDate to, SlotStatus status);

    List<GameSlot> findByGameIdAndStatusAndSlotDateGreaterThanEqual(
            Long gameId, SlotStatus status, LocalDate from);

    List<GameSlot> findByGameIdAndSlotDateBetween(Long gameId, LocalDate from, LocalDate to);

    boolean existsByGameIdAndSlotDate(Long gameId, LocalDate slotDate);

    /** Slots whose cutoff time has passed but haven't been allocated yet */
    @Query("SELECT s FROM GameSlot s WHERE s.status = 'AVAILABLE' AND s.slotStart > :now")
    List<GameSlot> findUnallocatedFutureSlots(LocalDateTime now);
}