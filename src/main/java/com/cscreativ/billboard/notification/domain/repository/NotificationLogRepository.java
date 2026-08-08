package com.cscreativ.billboard.notification.domain.repository;

import com.cscreativ.billboard.notification.domain.NotificationLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationLogRepository {
    NotificationLog save(NotificationLog log);
    Optional<NotificationLog> findById(UUID id);
    List<NotificationLog> findByRecipientId(UUID recipientId);
    int deleteReadOlderThan(LocalDateTime threshold);
}
