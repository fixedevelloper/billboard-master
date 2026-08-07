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
    private final EmailService emailService;
    private final NotificationBroadcaster broadcaster;

    public NotificationService(NotificationLogRepository logRepository, ApplicationEventPublisher eventPublisher,
                                EmailService emailService, NotificationBroadcaster broadcaster) {
        this.logRepository = logRepository;
        this.eventPublisher = eventPublisher;
        this.emailService = emailService;
        this.broadcaster = broadcaster;
    }

    @Transactional
    public NotificationLog sendNotification(UUID recipientId, String destination, String templateCode, NotificationChannel channel, Map<String, String> parameters) {
        Recipient recipient = new Recipient(recipientId, destination);
        String subject = humanizeTemplateCode(templateCode);
        String compiledContent = compileTemplate(templateCode, parameters);

        NotificationLog notificationLog = NotificationLog.createPending(recipient, channel, templateCode, compiledContent);

        boolean delivered;
        String failureReason = null;
        if (channel == NotificationChannel.EMAIL) {
            delivered = emailService.send(destination, subject, compiledContent);
            if (!delivered) {
                failureReason = "Échec de l'envoi de l'e-mail";
            }
        } else {
            // Aucun fournisseur branché pour ce canal (SMS, IN_APP, WEBHOOK) pour le moment.
            delivered = true;
        }

        if (delivered) {
            notificationLog.markAsSent();
        } else {
            notificationLog.markAsFailed(failureReason);
        }
        logRepository.save(notificationLog);

        if (delivered) {
            eventPublisher.publishEvent(new NotificationSentEvent(notificationLog.getId(), recipientId, channel, LocalDateTime.now()));
        }

        // Diffusion temps réel (cloche de notification) : tentée pour tout canal, pas
        // seulement IN_APP, dès lors qu'un navigateur du destinataire est connecté au flux SSE.
        broadcaster.publish(notificationLog);

        return notificationLog;
    }

    public NotificationLog getNotificationById(UUID id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification non trouvée avec l'id : " + id));
    }

    public List<NotificationLog> getNotificationsByRecipient(UUID recipientId) {
        return logRepository.findByRecipientId(recipientId);
    }

    public long getUnreadCount(UUID recipientId) {
        return logRepository.findByRecipientId(recipientId).stream().filter(n -> !n.isRead()).count();
    }

    @Transactional
    public NotificationLog markAsRead(UUID id) {
        NotificationLog notificationLog = getNotificationById(id);
        notificationLog.markAsRead();
        return logRepository.save(notificationLog);
    }

    private String humanizeTemplateCode(String templateCode) {
        String spaced = templateCode.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    private String compileTemplate(String templateCode, Map<String, String> parameters) {
        StringBuilder content = new StringBuilder("Bonjour,\n\n")
                .append(humanizeTemplateCode(templateCode))
                .append(".\n");
        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach((key, value) -> content.append("\n").append(key).append(" : ").append(value));
        }
        return content.toString();
    }
}
