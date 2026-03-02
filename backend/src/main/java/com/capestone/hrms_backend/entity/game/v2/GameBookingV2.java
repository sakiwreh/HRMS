package com.capestone.hrms_backend.entity.game.v2;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_bookings_v2")
@Getter
@Setter
public class GameBookingV2 extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameV2 game;

    @Column(name = "slot_start", nullable = false)
    private LocalDateTime slotStart;

    @Column(name = "slot_end", nullable = false)
    private LocalDateTime slotEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by", nullable = false)
    private Employee bookedBy;

    @Column(name = "booking_date_time", nullable = false)
    private LocalDateTime bookingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameBookingstatusV2 status = GameBookingstatusV2.PENDING;

    @Column(name = "priority_score",nullable = false)
    private int priorityScore;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameBookingParticipantV2> participants = new ArrayList<>();
}
