package com.cscreativ.billboard.creative.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreativeProofSubmittedEvent(UUID proofId, UUID campaignId, int version, LocalDateTime occurredOn) {}
