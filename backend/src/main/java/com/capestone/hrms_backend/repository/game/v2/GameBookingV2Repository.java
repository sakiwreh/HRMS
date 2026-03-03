package com.capestone.hrms_backend.repository.game.v2;

import com.capestone.hrms_backend.entity.game.v2.GameBookingV2;
import com.capestone.hrms_backend.entity.game.v2.GameBookingstatusV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface GameBookingV2Repository extends JpaRepository<GameBookingV2, Long> {

    //True if the game has an active or completed booking
    boolean existsByGameIdAndSlotStartAndStatusIn(Long gameId, LocalDateTime slotStart, List<GameBookingstatusV2> statuses);

    //Bookings for game and slot start
    List<GameBookingV2> findByGameIdAndSlotStart(Long gameId, LocalDateTime slotStart);

    //Bookings for a game where slot start falls in range
    @Query("SELECT b FROM GameBookingV2 b WHERE b.game.id = :gameId AND b.slotStart >= :from AND b.slotStart < :to")
    List<GameBookingV2> findByGameIdAndSlotStartBetween(Long gameId, LocalDateTime from, LocalDateTime to);

    //Bookings whose slotstart is within the cutoff (for scheduling purpose)
    List<GameBookingV2> findByStatusAndSlotStartLessThanEqual(GameBookingstatusV2 status, LocalDateTime cutoff);

    //Active bookings whose slot end has passed
    @Query("SELECT b FROM GameBookingV2 b WHERE b.status = 'ACTIVE' AND b.slotEnd <= :now")
    List<GameBookingV2> findCompletableBookings(LocalDateTime now);

    //Pending bookings whose slot start has passed
    @Query("SELECT b FROM GameBookingV2 b WHERE b.status = 'PENDING' AND b.slotStart <= :now")
    List<GameBookingV2> findExpiredPendingBookings(LocalDateTime now);

    //My bookings as own created
    List<GameBookingV2> findByBookedByIdOrderBySlotStartDesc(Long employeeId);

    //My bookings as participant
    @Query("SELECT b FROM GameBookingV2 b JOIN b.participants p WHERE p.employee.id = :empId ORDER BY b.slotStart DESC")
    List<GameBookingV2> findByParticipantEmployeeId(Long empId);

    //Check if employee already has an Active booking on a given date for any game
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM GameBookingV2 b " +
            "JOIN b.participants p WHERE p.employee.id = :empId " +
            "AND b.status in ('ACTIVE','COMPLETED') " +
            "AND b.slotStart >= :dayStart AND b.slotStart < :dayEnd")
    boolean hasActiveAndCompletedBookingOnDate(Long empId, LocalDateTime dayStart, LocalDateTime dayEnd);

    //Pending or active request for same slot
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM GameBookingV2 b " +
            "JOIN b.participants p WHERE p.employee.id = :empId " +
            "AND b.game.id = :gameId AND b.slotStart = :slotStart " +
            "AND b.status IN ('ACTIVE', 'PENDING')")
    boolean hasPendingOrActiveForSlot(Long empId, Long gameId, LocalDateTime slotStart);

    @Query("SELECT b FROM GameBookingV2 b WHERE b.slotStart > :now and b.slotEnd <= :eod and b.status in ('ACTIVE','COMPLETED')")
    List<GameBookingV2> findUpcomingMatchesToday(LocalDateTime now, LocalDateTime eod);
}