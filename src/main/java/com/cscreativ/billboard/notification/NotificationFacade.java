package com.cscreativ.billboard.notification;

import java.util.Map;
import java.util.UUID;

public interface NotificationFacade {
    /** channel : "EMAIL", "SMS", "IN_APP" ou "WEBHOOK" (voir NotificationChannel, interne au module). */
    void sendNotification(UUID recipientId, String recipientAddress, String templateCode, String channel, Map<String, String> parameters);
}
