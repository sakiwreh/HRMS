package com.capestone.hrms_backend.entity.game;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "game_waitlist_participants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"waitlist_id", "employee_id"})
})
@Getter
@Setter
public class GameWaitListParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waitlist_id", nullable = false)
    private GameWaitList waitlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
