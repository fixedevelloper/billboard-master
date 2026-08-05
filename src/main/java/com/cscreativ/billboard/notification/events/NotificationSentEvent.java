package com.cscreativ.billboard.notification.events;

import com.cscreativ.billboard.notification.domain.NotificationChannel;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSentEvent(UUID notificationId, UUID recipientId, NotificationChannel channel, LocalDateTime occurredOn) {}
