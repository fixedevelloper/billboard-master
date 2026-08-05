package com.cscreativ.billboard.creative.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreativeProofApprovedEvent(UUID proofId, UUID campaignId, LocalDateTime occurredOn) {}
