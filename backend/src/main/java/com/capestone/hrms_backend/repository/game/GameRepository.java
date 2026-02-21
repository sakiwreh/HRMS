package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game,Long> {
    List<Game> findByActiveTrue();
}
