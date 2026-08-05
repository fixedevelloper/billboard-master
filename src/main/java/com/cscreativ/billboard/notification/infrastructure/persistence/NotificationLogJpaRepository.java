package com.cscreativ.billboard.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, UUID> {
    List<NotificationLogEntity> findByRecipientId(UUID recipientId);
}
