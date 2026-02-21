package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameCycleRepository extends JpaRepository<GameCycle,Long> {
    Optional<GameCycle> findByGameIdAndEndedAtIsNull(Long gameId);

    Optional<GameCycle> findTopByGameIdOrderByCycleNumberDesc(Long gameId);
}
