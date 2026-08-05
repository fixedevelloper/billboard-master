package com.cscreativ.billboard.campaign.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CampaignRejectedEvent(UUID campaignId, String reason, LocalDateTime occurredOn) {}
