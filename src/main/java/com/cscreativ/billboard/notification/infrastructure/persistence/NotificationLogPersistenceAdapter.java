package com.cscreativ.billboard.notification.infrastructure.persistence;

import com.cscreativ.billboard.notification.domain.NotificationLog;
import com.cscreativ.billboard.notification.domain.repository.NotificationLogRepository;
import com.cscreativ.billboard.notification.domain.valueobject.Recipient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NotificationLogPersistenceAdapter implements NotificationLogRepository {

    private final NotificationLogJpaRepository jpaRepository;

    public NotificationLogPersistenceAdapter(NotificationLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NotificationLog save(NotificationLog log) {
        NotificationLogEntity entity = toEntity(log);
        NotificationLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<NotificationLog> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<NotificationLog> findByRecipientId(UUID recipientId) {
        return jpaRepository.findByRecipientId(recipientId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private NotificationLogEntity toEntity(NotificationLog domain) {
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setId(domain.getId());
        entity.setRecipientId(domain.getRecipient().getRecipientId());
        entity.setDestination(domain.getRecipient().getDestination());
        entity.setChannel(domain.getChannel());
        entity.setTemplateCode(domain.getTemplateCode());
        entity.setContent(domain.getContent());
        entity.setStatus(domain.getStatus());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setRead(domain.isRead());
        entity.setSentAt(domain.getSentAt());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private NotificationLog toDomain(NotificationLogEntity entity) {
        Recipient recipient = new Recipient(entity.getRecipientId(), entity.getDestination());
        return new NotificationLog(
                entity.getId(),
                recipient,
                entity.getChannel(),
                entity.getTemplateCode(),
                entity.getContent(),
                entity.getStatus(),
                entity.getErrorMessage(),
                entity.isRead(),
                entity.getSentAt(),
                entity.getCreatedAt()
        );
    }
}
