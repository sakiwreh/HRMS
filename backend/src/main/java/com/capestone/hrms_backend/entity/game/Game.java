package com.capestone.hrms_backend.entity.game;

import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name="games")
@Getter
@Setter
public class Game extends Base {
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

    @Column(name="slot_generation_days",nullable = false)
    private Integer slotGenerationDays;
}
