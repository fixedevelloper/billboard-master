package com.cscreativ.billboard.creative.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreativeRevisionRequestedEvent(UUID proofId, UUID campaignId, String feedback, LocalDateTime occurredOn) {}
