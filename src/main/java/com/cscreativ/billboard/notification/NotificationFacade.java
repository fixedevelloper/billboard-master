package com.cscreativ.billboard.notification;

import com.cscreativ.billboard.notification.domain.NotificationChannel;

import java.util.Map;
import java.util.UUID;

public interface NotificationFacade {
    void sendNotification(UUID recipientId, String recipientAddress, String templateCode, NotificationChannel channel, Map<String, String> parameters);
}
