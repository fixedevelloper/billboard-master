package com.cscreativ.billboard.installation.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstallationCompletedEvent(UUID taskId, UUID campaignId, UUID billboardId, UUID photoFileId, LocalDateTime occurredOn) {}
