package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameWaitList;
import com.capestone.hrms_backend.entity.game.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameWaitlistRepository extends JpaRepository<GameWaitList,Long> {
    List<GameWaitList> findBySlotIdAndStatusOrderByPriorityScoreAscAppliedDateTimeAscIdAsc(
            Long slotId, WaitlistStatus status);

    List<GameWaitList> findByGameIdAndStatus(Long gameId, WaitlistStatus status);

    List<GameWaitList> findByRequestedByIdAndStatus(Long employeeId, WaitlistStatus status);

    boolean existsBySlotIdAndRequestedByIdAndStatus(Long slotId, Long employeeId, WaitlistStatus status);

    List<GameWaitList> findByRequestedByIdOrderByAppliedDateTimeDesc(Long employeeId);

    @Query("SELECT w FROM GameWaitList w WHERE w.status = 'WAIT' AND w.slot.slotStart <= :now")
    List<GameWaitList> findExpiredWaitEntries(LocalDateTime now);
}
