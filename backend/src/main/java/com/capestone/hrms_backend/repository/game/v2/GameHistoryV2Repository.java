package com.capestone.hrms_backend.repository.game.v2;

import com.capestone.hrms_backend.entity.game.v2.GameHistoryV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameHistoryV2Repository extends JpaRepository<GameHistoryV2, Long> {
    Optional<GameHistoryV2> findByGameIdAndEmployeeIdAndCycleNumber(Long gameId, Long empId, int cycleNumber);

    //Counting employees who played at least 1 time in current cycle, if count == interested players, increase cycle
    long countByGameIdAndCycleNumberAndPlayCountGreaterThanEqual(Long gameId, int cycleNumber, int min);
}
