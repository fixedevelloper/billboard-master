package com.cscreativ.billboard.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, UUID> {
    List<NotificationLogEntity> findByRecipientId(UUID recipientId);

    @Modifying
    @Query("delete from NotificationLogEntity n where n.isRead = true and n.createdAt < :threshold")
    int deleteByReadTrueAndCreatedAtBefore(@Param("threshold") LocalDateTime threshold);
}
