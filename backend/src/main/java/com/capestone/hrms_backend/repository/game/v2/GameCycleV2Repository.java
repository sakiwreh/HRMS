package com.capestone.hrms_backend.repository.game.v2;

import com.capestone.hrms_backend.entity.game.v2.GameCycleV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameCycleV2Repository extends JpaRepository<GameCycleV2, Long> {
    Optional<GameCycleV2> findByGameIdAndEndedAtIsNull(Long gameId);
}
