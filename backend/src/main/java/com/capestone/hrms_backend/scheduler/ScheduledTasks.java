package com.capestone.hrms_backend.scheduler;

import com.capestone.hrms_backend.dto.response.GameResponseDto;
import com.capestone.hrms_backend.service.IGameAdminService;
import com.capestone.hrms_backend.service.IGamePlayService;
import com.capestone.hrms_backend.service.ISocialCelebrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    public final ISocialCelebrationService celebrationService;
    public final IGameAdminService gameAdminService;
    public final IGamePlayService gamePlayService;


    @PostConstruct
    public void runOnStartup() {
        try {
            log.info("Running initial setup tasks on startup");
            celebrationService.runDaily();
            nightlyGenerateSlots();
        } catch (Exception e) {
            log.error("runOnStartup failed", e);
        }
    }

    @Scheduled(cron = "0 5 0 * * *")
    //second minutes hour dayOfMonth month dayOfWeek
    public void dailyCelebrations() {
        try {
            log.info("Running daily celebrations job");
            celebrationService.runDaily();
        } catch (Exception e) {
            log.error("dailyCelebrations failed", e);
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void nightlyGenerateSlots() {
        try {
            log.info("Running nightly slot generation");
            List<GameResponseDto> games = gameAdminService.getActiveGames();
            LocalDate today = LocalDate.now();
            for (GameResponseDto g : games) {
                int days = g.getSlotGenerationDays();
                for (int d = 1; d <= days; d++) {
                    LocalDate date = today.plusDays(d);
                    try {
                        gameAdminService.generateSlotsForDate(g.getId(), date);
                    } catch (Exception ex) {
                        log.warn("Failed to generate slots for game {} on {}: {}", g.getId(), date, ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("nightlyGenerateSlots failed", e);
        }
    }

    //Finalize expired waitlist entries and complete bookings every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void processGameWaitlistAndBookings() {
        try {
            log.info("Processing game bookings and waitlist");
            gamePlayService.completeExpiredBookings();
            gamePlayService.finalizeExpiredWaitlistEntries();
        } catch (Exception e) {
            log.error("processGameWaitlistAndBookings failed", e);
        }
    }


}
