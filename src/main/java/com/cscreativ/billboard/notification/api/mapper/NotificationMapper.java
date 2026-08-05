package com.cscreativ.billboard.notification.api.mapper;

import com.cscreativ.billboard.notification.api.response.NotificationLogResponse;
import com.cscreativ.billboard.notification.domain.NotificationLog;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationLogResponse toResponse(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(),
                log.getRecipient().getRecipientId(),
                log.getRecipient().getDestination(),
                log.getChannel().name(),
                log.getTemplateCode(),
                log.getContent(),
                log.getStatus().name(),
                log.getErrorMessage(),
                log.getSentAt(),
                log.getCreatedAt()
        );
    }
}
