package com.capestone.hrms_backend.entity.game.v2;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "game_interest_v2", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id","game_id"}))
@Getter
@Setter
public class GameInterestV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id",nullable = false)
    private GameV2 game;

}
