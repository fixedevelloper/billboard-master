package com.cscreativ.billboard.admin.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {
    private UUID id;
    private UUID adminId;
    private AuditAction action;
    private String targetEntity;
    private UUID targetId;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog(UUID id, UUID adminId, AuditAction action, String targetEntity, UUID targetId, String details, LocalDateTime timestamp) {
        this.id = id;
        this.adminId = adminId;
        this.action = action;
        this.targetEntity = targetEntity;
        this.targetId = targetId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static AuditLog create(UUID adminId, AuditAction action, String targetEntity, UUID targetId, String details) {
        return new AuditLog(UUID.randomUUID(), adminId, action, targetEntity, targetId, details, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getAdminId() { return adminId; }
    public AuditAction getAction() { return action; }
    public String getTargetEntity() { return targetEntity; }
    public UUID getTargetId() { return targetId; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
