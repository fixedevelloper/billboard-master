package com.cscreativ.billboard.admin.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminActionPerformedEvent(UUID adminId, String action, String targetEntity, UUID targetId, LocalDateTime occurredOn) {}
