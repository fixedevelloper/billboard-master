package com.cscreativ.billboard.notification.domain;

import com.cscreativ.billboard.notification.domain.valueobject.Recipient;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationLog {
    private UUID id;
    private Recipient recipient;
    private NotificationChannel channel;
    private String templateCode;
    private String content;
    private NotificationStatus status;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    public NotificationLog(UUID id, Recipient recipient, NotificationChannel channel, String templateCode, String content, NotificationStatus status, String errorMessage, LocalDateTime sentAt, LocalDateTime createdAt) {
        this.id = id;
        this.recipient = recipient;
        this.channel = channel;
        this.templateCode = templateCode;
        this.content = content;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
    }

    public static NotificationLog createPending(Recipient recipient, NotificationChannel channel, String templateCode, String content) {
        LocalDateTime now = LocalDateTime.now();
        return new NotificationLog(UUID.randomUUID(), recipient, channel, templateCode, content, NotificationStatus.PENDING, null, null, now);
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = error;
    }

    public UUID getId() { return id; }
    public Recipient getRecipient() { return recipient; }
    public NotificationChannel getChannel() { return channel; }
    public String getTemplateCode() { return templateCode; }
    public String getContent() { return content; }
    public NotificationStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
