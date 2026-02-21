package com.capestone.hrms_backend.entity.game;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_bookings")
@Getter
@Setter
public class GameBooking extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private GameSlot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by", nullable = false)
    private Employee bookedBy;

    @Column(name = "booking_date_time", nullable = false)
    private LocalDateTime bookingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.ACTIVE;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameBookingParticipant> participants = new ArrayList<>();
}
