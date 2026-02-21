package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameHistoryRepository extends JpaRepository<GameHistory,Long> {

    Optional<GameHistory> findByGameIdAndEmployeeIdAndCycleNumber(Long gameId, Long employeeId, int cycleNumber);

    List<GameHistory> findByGameIdAndCycleNumber(Long gameId, int cycleNumber);

    long countByGameIdAndCycleNumberAndPlayCountGreaterThanEqual(Long gameId, int cycleNumber, int minPlayCount);
}
