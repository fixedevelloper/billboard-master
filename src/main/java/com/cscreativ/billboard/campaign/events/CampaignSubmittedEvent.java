package com.cscreativ.billboard.campaign.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CampaignSubmittedEvent(UUID campaignId, UUID advertiserId, LocalDateTime occurredOn) {}
