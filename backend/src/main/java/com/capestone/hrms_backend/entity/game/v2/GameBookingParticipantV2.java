package com.capestone.hrms_backend.entity.game.v2;

import com.capestone.hrms_backend.entity.organization.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "game_booking_participants_v2", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_id", "employee_id"})
})
@Getter
@Setter
public class GameBookingParticipantV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private GameBookingV2 booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
