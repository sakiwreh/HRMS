package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.GameWaitListParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameWaitlistParticipantRepository extends JpaRepository<GameWaitListParticipant,Long> {

    @Query("SELECT wp.employee.id FROM GameWaitListParticipant wp WHERE wp.waitlist.slot.id = :slotId AND wp.waitlist.status IN ('WAIT','ALLOCATED')")
    List<Long> findActiveParticipantEmployeeIdsBySlotId(Long slotId);
}
