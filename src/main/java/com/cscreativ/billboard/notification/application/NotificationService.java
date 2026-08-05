package com.cscreativ.billboard.notification.application;

import com.cscreativ.billboard.notification.domain.NotificationChannel;
import com.cscreativ.billboard.notification.domain.NotificationLog;
import com.cscreativ.billboard.notification.domain.exception.NotificationNotFoundException;
import com.cscreativ.billboard.notification.domain.repository.NotificationLogRepository;
import com.cscreativ.billboard.notification.domain.valueobject.Recipient;
import com.cscreativ.billboard.notification.events.NotificationSentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationLogRepository logRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(NotificationLogRepository logRepository, ApplicationEventPublisher eventPublisher) {
        this.logRepository = logRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public NotificationLog sendNotification(UUID recipientId, String destination, String templateCode, NotificationChannel channel, Map<String, String> parameters) {
        Recipient recipient = new Recipient(recipientId, destination);
        String compiledContent = compileTemplate(templateCode, parameters);

        NotificationLog log = NotificationLog.createPending(recipient, channel, templateCode, compiledContent);
        
        try {
            // Logique de dispatch d'envoi réseau (Email provider, SMS Gateway...)
            log.markAsSent();
            logRepository.save(log);

            eventPublisher.publishEvent(new NotificationSentEvent(log.getId(), recipientId, channel, LocalDateTime.now()));
        } catch (Exception e) {
            log.markAsFailed(e.getMessage());
            logRepository.save(log);
        }

        return log;
    }

    public NotificationLog getNotificationById(UUID id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification non trouvée avec l'id : " + id));
    }

    public List<NotificationLog> getNotificationsByRecipient(UUID recipientId) {
        return logRepository.findByRecipientId(recipientId);
    }

    private String compileTemplate(String templateCode, Map<String, String> parameters) {
        StringBuilder content = new StringBuilder("Template: " + templateCode);
        if (parameters != null && !parameters.isEmpty()) {
            content.append(" | Params: ").append(parameters.toString());
        }
        return content.toString();
    }
}
