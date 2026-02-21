package com.capestone.hrms_backend.entity.game;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "emp_id", "cycle_number"})
})
@Getter
@Setter
public class GameHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "cycle_number", nullable = false)
    private int cycleNumber;

    @Column(name = "play_count", nullable = false)
    private int playCount = 0;

    @Column(name = "last_played_at")
    private LocalDateTime lastPlayedAt;
}
