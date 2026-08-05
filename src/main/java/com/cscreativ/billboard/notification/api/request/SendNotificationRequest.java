package com.cscreativ.billboard.notification.api.request;

import com.cscreativ.billboard.notification.domain.NotificationChannel;

import java.util.Map;
import java.util.UUID;

public record SendNotificationRequest(
        UUID recipientId,
        String destination,
        String templateCode,
        NotificationChannel channel,
        Map<String, String> parameters
) {}
