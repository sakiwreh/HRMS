package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameInterestRepository extends JpaRepository<GameInterest,Long> {
    boolean existsByEmployeeIdAndGameId(Long employeeId, Long gameId);

    Optional<GameInterest> findByEmployeeIdAndGameId(Long employeeId, Long gameId);

    List<GameInterest> findByGameId(Long gameId);

    List<GameInterest> findByEmployeeId(Long employeeId);

    long countByGameId(Long gameId);
}
