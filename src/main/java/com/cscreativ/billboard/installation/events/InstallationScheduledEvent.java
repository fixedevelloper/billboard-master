package com.cscreativ.billboard.installation.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstallationScheduledEvent(UUID taskId, UUID campaignId, UUID billboardId, LocalDateTime scheduledDate, LocalDateTime occurredOn) {}
