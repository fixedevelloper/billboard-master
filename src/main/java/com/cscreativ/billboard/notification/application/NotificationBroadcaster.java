package com.cscreativ.billboard.notification.application;

import com.cscreativ.billboard.notification.api.mapper.NotificationMapper;
import com.cscreativ.billboard.notification.domain.NotificationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Diffuse chaque notification en temps réel aux navigateurs connectés (cloche de
 * notification) via Server-Sent Events. Registre en mémoire uniquement — un redémarrage
 * de l'instance ferme les connexions actives (le navigateur les rouvre automatiquement) ;
 * suffisant pour une seule instance applicative, une file de messages (Redis pub/sub...)
 * serait nécessaire pour un déploiement multi-instances.
 */
@Component
public class NotificationBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(NotificationBroadcaster.class);
    private static final long EMITTER_TIMEOUT_MS = 30L * 60 * 1000; // 30 min ; le client se reconnecte ensuite

    private final Map<UUID, List<SseEmitter>> emittersByRecipient = new ConcurrentHashMap<>();
    private final NotificationMapper notificationMapper;

    public NotificationBroadcaster(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public SseEmitter subscribe(UUID recipientId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        List<SseEmitter> emitters = emittersByRecipient.computeIfAbsent(recipientId, id -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        Runnable cleanup = () -> emitters.remove(emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(throwable -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            cleanup.run();
        }

        return emitter;
    }

    public void publish(NotificationLog notificationLog) {
        UUID recipientId = notificationLog.getRecipient().getRecipientId();
        List<SseEmitter> emitters = emittersByRecipient.get(recipientId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notificationMapper.toResponse(notificationLog)));
            } catch (IOException e) {
                log.debug("Émetteur SSE fermé pour le destinataire {}, retrait", recipientId);
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}
