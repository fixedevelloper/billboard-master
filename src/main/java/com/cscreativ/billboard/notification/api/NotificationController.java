package com.cscreativ.billboard.notification.api;

import com.cscreativ.billboard.notification.api.mapper.NotificationMapper;
import com.cscreativ.billboard.notification.api.request.SendNotificationRequest;
import com.cscreativ.billboard.notification.api.response.NotificationLogResponse;
import com.cscreativ.billboard.notification.application.NotificationService;
import com.cscreativ.billboard.notification.domain.NotificationLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationLogResponse> sendNotification(@RequestBody SendNotificationRequest request) {
        NotificationLog log = notificationService.sendNotification(
                request.recipientId(),
                request.destination(),
                request.templateCode(),
                request.channel(),
                request.parameters()
        );
        return ResponseEntity.ok(notificationMapper.toResponse(log));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationLogResponse> getNotificationById(@PathVariable UUID id) {
        NotificationLog log = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notificationMapper.toResponse(log));
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<NotificationLogResponse>> getNotificationsByRecipient(@PathVariable UUID recipientId) {
        List<NotificationLog> logs = notificationService.getNotificationsByRecipient(recipientId);
        return ResponseEntity.ok(logs.stream().map(notificationMapper::toResponse).collect(Collectors.toList()));
    }
}
