package com.capestone.hrms_backend.repository.game;

import com.capestone.hrms_backend.entity.game.BookingStatus;
import com.capestone.hrms_backend.entity.game.GameBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameBookingRepository extends JpaRepository<GameBooking,Long> {
    @Query("SELECT b FROM GameBooking b WHERE b.slot.slotEnd <= :now AND b.status = 'ACTIVE'")
    List<GameBooking> findCompletableBookings(LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM GameBooking b JOIN b.participants bp WHERE bp.employee.id = :empId AND b.game.id = :gameId AND b.slot.slotDate = :date AND b.status = 'ACTIVE'")
    boolean hasActiveBookingForGameOnDate(Long empId, Long gameId, LocalDate date);

    List<GameBooking> findBySlotIdAndStatus(Long slotId, BookingStatus status);

    @Query("SELECT b FROM GameBooking b JOIN b.participants bp WHERE bp.employee.id = :empId ORDER BY b.bookingDateTime DESC")
    List<GameBooking> findByParticipantEmployeeId(Long empId);

    List<GameBooking> findBySlotId(Long slotId);
}
