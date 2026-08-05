package com.cscreativ.billboard.notification.application;

import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.notification.domain.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class NotificationFacadeImpl implements NotificationFacade {

    private final NotificationService notificationService;

    public NotificationFacadeImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void sendNotification(UUID recipientId, String recipientAddress, String templateCode, NotificationChannel channel, Map<String, String> parameters) {
        notificationService.sendNotification(recipientId, recipientAddress, templateCode, channel, parameters);
    }
}
