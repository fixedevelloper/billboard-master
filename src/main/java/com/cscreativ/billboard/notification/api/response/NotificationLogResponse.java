package com.cscreativ.billboard.notification.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationLogResponse(
        UUID id,
        UUID recipientId,
        String destination,
        String channel,
        String templateCode,
        String content,
        String status,
        String errorMessage,
        boolean read,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {}
