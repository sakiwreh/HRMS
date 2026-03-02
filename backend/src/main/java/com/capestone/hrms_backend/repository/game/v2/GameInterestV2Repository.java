package com.capestone.hrms_backend.repository.game.v2;

import com.capestone.hrms_backend.entity.game.v2.GameInterestV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameInterestV2Repository extends JpaRepository<GameInterestV2, Long> {
    boolean existsByEmployeeIdAndGameId(Long empId, Long gameId);
    Optional<GameInterestV2> findByEmployeeIdAndGameId(Long empId, Long gameId);
    List<GameInterestV2> findByEmployeeId(Long empId);
    List<GameInterestV2> findByGameId(Long gameId);
    Long countByGameId(Long gameId);
}
