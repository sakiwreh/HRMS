package com.capestone.hrms_backend.repository.game.v2;

import com.capestone.hrms_backend.entity.game.v2.GameV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameV2Repository extends JpaRepository<GameV2,Long> {
    List<GameV2> findByActiveTrue();
}
