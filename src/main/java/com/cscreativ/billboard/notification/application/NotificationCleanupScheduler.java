package com.cscreativ.billboard.notification.application;

import com.cscreativ.billboard.notification.domain.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Purge les notifications déjà lues restées trop longtemps en base. Le délai (en jours,
 * à partir de la création de la notification) est configurable via
 * NOTIFICATION_READ_RETENTION_DAYS (voir .env / application.properties). Les notifications
 * non lues ne sont jamais supprimées automatiquement, quel que soit leur âge.
 */
@Component
public class NotificationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationCleanupScheduler.class);

    private final NotificationLogRepository notificationLogRepository;
    private final int readRetentionDays;

    public NotificationCleanupScheduler(NotificationLogRepository notificationLogRepository,
                                         @Value("${notification.read-retention-days}") int readRetentionDays) {
        this.notificationLogRepository = notificationLogRepository;
        this.readRetentionDays = readRetentionDays;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void cleanupReadNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(readRetentionDays);
        int deleted = notificationLogRepository.deleteReadOlderThan(threshold);
        if (deleted > 0) {
            log.info("{} notification(s) lue(s) de plus de {} jour(s) supprimée(s)", deleted, readRetentionDays);
        }
    }
}
