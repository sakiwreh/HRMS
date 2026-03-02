package com.capestone.hrms_backend.entity.game.v2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "games_v2")
@Getter
@Setter
public class GameV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @Column(name = "max_duration_mins", nullable = false)
    private Integer maxDurationMins;

    @Column(name = "max_players_per_slot", nullable = false)
    private Integer maxPlayersPerSlot;

    @Column(name = "cancellation_before_mins", nullable = false)
    private Integer cancellationBeforeMins;
}
